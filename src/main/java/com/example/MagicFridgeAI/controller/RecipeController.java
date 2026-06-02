package com.example.MagicFridgeAI.controller;

import com.example.MagicFridgeAI.dto.RecipeDTO;
import com.example.MagicFridgeAI.service.ChatGptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class RecipeController {

    private final ChatGptService chatGptService;

    public RecipeController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @GetMapping("/generate")
    public Mono<ResponseEntity<RecipeDTO>> generateRecipe() {
        return chatGptService.generateRecipe()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.internalServerError().body(errorRecipe(e.getMessage()))
                ));
    }

    private RecipeDTO errorRecipe(String message) {
        RecipeDTO recipe = new RecipeDTO();
        recipe.setTitulo("Erro ao gerar receita");
        recipe.setResumo(message);
        return recipe;
    }
}
