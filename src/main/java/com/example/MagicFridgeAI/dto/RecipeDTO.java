package com.example.MagicFridgeAI.dto;

import java.util.List;

public class RecipeDTO {
    private String titulo;
    private String resumo;
    private List<String> ingredientes;
    private List<String> preparo;
    private List<String> observacoes;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<String> getPreparo() {
        return preparo;
    }

    public void setPreparo(List<String> preparo) {
        this.preparo = preparo;
    }

    public List<String> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(List<String> observacoes) {
        this.observacoes = observacoes;
    }
}
