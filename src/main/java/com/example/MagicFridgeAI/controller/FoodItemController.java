package com.example.MagicFridgeAI.controller;

import com.example.MagicFridgeAI.dto.FoodItemDTO;
import com.example.MagicFridgeAI.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class FoodItemController {

    private final FoodItemService service;

    public FoodItemController(FoodItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FoodItemDTO> createFood(@Valid @RequestBody FoodItemDTO food) {
        FoodItemDTO newFood = service.createFood(food);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFood);
    }

    @GetMapping
    public ResponseEntity<List<FoodItemDTO>> listAllFoods() {
        List<FoodItemDTO> listFood = service.listAllFoods();
        if(listFood.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(listFood);
    }

    @GetMapping("{id}")
    public ResponseEntity<FoodItemDTO> getFoodById (@PathVariable Long id) {
        FoodItemDTO food = service.GetFoodById(id);
        return ResponseEntity.ok(food);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FoodItemDTO> editFood(@PathVariable Long id, @Valid @RequestBody FoodItemDTO food) {
        FoodItemDTO updateFood = service.editFood(id, food);
        return ResponseEntity.ok(updateFood);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteFood(@PathVariable Long id) {
        service.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
