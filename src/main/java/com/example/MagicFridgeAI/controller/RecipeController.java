package com.example.MagicFridgeAI.controller;

import com.example.MagicFridgeAI.service.ChatGptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final ChatGptService chatGptService;

    public RecipeController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @GetMapping("/generate")
    public Mono<ResponseEntity<String>> generateRecipe() {
        return chatGptService.generateRecipe()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.internalServerError()
                                .body("Erro ao gerar receita: " + e.getMessage())
                ));
    }

}
