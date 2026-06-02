package com.example.MagicFridgeAI.service;

import com.example.MagicFridgeAI.dto.FoodItemDTO;
import com.example.MagicFridgeAI.mapper.FoodItemMapper;
import com.example.MagicFridgeAI.model.FoodItem;
import com.example.MagicFridgeAI.repository.FoodItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemService {

    private final FoodItemRepository repository;
    private final FoodItemMapper mapper;

    public FoodItemService(FoodItemRepository repository, FoodItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FoodItemDTO createFood(FoodItemDTO food) {
        FoodItem newFood = mapper.map(food);
        return mapper.map(repository.save(newFood));
    }

    public List<FoodItemDTO> listAllFoods() {
        List<FoodItem> listFood = repository.findAll();
        return listFood.stream()
                .map(mapper::map)
                .toList();
    }

    public FoodItemDTO GetFoodById(Long id) {
        FoodItem food = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id não encontrado"));
        return mapper.map(food);
    }

    @Transactional
    public FoodItemDTO editFood(Long id, FoodItemDTO food) {
        FoodItem possibleFood = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id não encontrado"));

        if(food.getValidade() != null) possibleFood.setValidade(food.getValidade());
        if(food.getCategoria() != null) possibleFood.setCategoria(food.getCategoria());
        if(food.getNome() != null) possibleFood.setNome(food.getNome());
        if(food.getQuantidade() != null) possibleFood.setQuantidade(food.getQuantidade());
        if(food.getUnidade() != null) possibleFood.setUnidade(food.getUnidade());

        return mapper.map(possibleFood);
    }

    public void deleteFood(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException("Id não encontrado");
        repository.deleteById(id);
    }
}
