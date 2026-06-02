package com.example.MagicFridgeAI.dto;
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
    private String categoria;
    @NotNull(message = "A quantidade é obrigatoria")
    @Min(value = 1, message = "A quantidade tem que ser maior que 1")
    private Integer quantidade;
    @NotBlank(message = "A unidade é obrigatoria")
    private String unidade;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }
}
