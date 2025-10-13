package com.example.MagicFridgeAI.service;

import com.example.MagicFridgeAI.dto.FoodItemDTO;
import com.example.MagicFridgeAI.repository.FoodItemRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
    private String apiKey = System.getenv("API_KEY");

    public ChatGptService(FoodItemRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public Mono<String> generateRecipe(){

        List<FoodItemDTO> foodList = repository.findAll().stream()
                .map(item -> new FoodItemDTO(item.getId(), item.getNome(), item.getCategoria(), item.getQuantidade() ,item.getValidade()))
                .toList();

        String ingredientesString = foodList.stream()
                .map(FoodItemDTO::getNome)
                .collect(Collectors.joining(", "));

        String prompt = "Você é um assistente culinário que cria receitas práticas a partir dos ingredientes fornecidos. " +
                "Tenho os seguintes ingredientes: " + ingredientesString;

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
                .map(response ->
                        response.path("output").get(0)
                                .path("content").get(0)
                                .path("text").asText());
     }


}
