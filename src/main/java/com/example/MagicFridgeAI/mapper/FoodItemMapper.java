package com.example.MagicFridgeAI.mapper;

import com.example.MagicFridgeAI.dto.FoodItemDTO;
import com.example.MagicFridgeAI.model.FoodItem;
import org.springframework.stereotype.Component;

@Component
public class FoodItemMapper {
    public FoodItem map(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(foodItemDTO.getId());
        foodItem.setCategoria(foodItemDTO.getCategoria());
        foodItem.setNome(foodItemDTO.getNome());
        foodItem.setQuantidade(foodItemDTO.getQuantidade());
        foodItem.setValidade(foodItemDTO.getValidade());

        return foodItem;
    }

    public FoodItemDTO map(FoodItem foodItem) {
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        foodItemDTO.setId(foodItem.getId());
        foodItemDTO.setCategoria(foodItem.getCategoria());
        foodItemDTO.setNome(foodItem.getNome());
        foodItemDTO.setQuantidade(foodItem.getQuantidade());
        foodItemDTO.setValidade(foodItem.getValidade());

        return foodItemDTO;
    }
}
