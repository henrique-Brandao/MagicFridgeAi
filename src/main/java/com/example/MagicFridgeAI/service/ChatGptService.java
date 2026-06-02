package com.example.MagicFridgeAI.service;

import com.example.MagicFridgeAI.dto.FoodItemDTO;
import com.example.MagicFridgeAI.dto.RecipeDTO;
import com.example.MagicFridgeAI.repository.FoodItemRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatGptService {
    private final FoodItemRepository repository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public ChatGptService(
            FoodItemRepository repository,
            WebClient webClient,
            ObjectMapper objectMapper,
            @Value("${chatgpt.api.key:}") String apiKey
    ) {
        this.repository = repository;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public Mono<RecipeDTO> generateRecipe(){
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("API_KEY nao configurada. Defina a variavel de ambiente API_KEY para gerar receitas."));
        }

        List<FoodItemDTO> foodList = repository.findAll().stream()
                .map(item -> new FoodItemDTO(
                        item.getId(),
                        item.getNome(),
                        item.getCategoria(),
                        item.getQuantidade(),
                        item.getUnidade(),
                        item.getValidade()
                ))
                .toList();

        String ingredientesString = foodList.stream()
                .map(item -> {
                    String categoria = item.getCategoria() == null || item.getCategoria().isBlank()
                            ? ""
                            : " (" + item.getCategoria() + ")";
                    return item.getQuantidade() + " " + item.getUnidade() + " de " + item.getNome() + categoria;
                })
                .collect(Collectors.joining(", "));

        String prompt = """
                Voce e um assistente culinario que cria receitas praticas a partir dos ingredientes disponiveis.
                Ingredientes disponiveis: %s.
                Responda somente com JSON valido, sem markdown, no formato:
                {
                  "titulo": "nome curto da receita",
                  "resumo": "uma frase sobre a receita",
                  "ingredientes": ["ingrediente com quantidade usada"],
                  "preparo": ["passo 1", "passo 2"],
                  "observacoes": ["dica, substituicao ou alerta curto"]
                }
                """.formatted(ingredientesString);

        Map<String, Object> requestBody = Map.of(
            "model", "gpt-4o-mini",
                "input", prompt
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.path("output").get(0)
                                .path("content").get(0)
                                .path("text").asText())
                .map(this::parseRecipe);
     }

    private RecipeDTO parseRecipe(String recipeText) {
        try {
            String json = recipeText
                    .replaceFirst("^```json\\s*", "")
                    .replaceFirst("^```\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
            return objectMapper.readValue(json, RecipeDTO.class);
        } catch (Exception e) {
            RecipeDTO fallback = new RecipeDTO();
            fallback.setTitulo("Receita sugerida");
            fallback.setResumo(recipeText);
            fallback.setIngredientes(List.of());
            fallback.setPreparo(List.of(recipeText));
            fallback.setObservacoes(List.of("A IA retornou uma resposta fora do formato esperado."));
            return fallback;
        }
    }
}
