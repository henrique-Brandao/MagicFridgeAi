package com.example.MagicFridgeAI.dto;

import com.example.MagicFridgeAI.enums.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class FoodItemDTO {

    private Long id;
    @NotBlank(message = "O nome é obrigatorio")
    private String nome;
    @NotNull(message = "A categoria é obrigatoria")
    private Categoria categoria;
    @NotNull(message = "A quantidade é obrigatoria")
    @Min(value = 1, message = "A quantidade tem que ser maior que 1")
    private Integer quantidade;
    @NotNull(message = "A validade é obrigatoria")
    private LocalDate validade;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }
}
