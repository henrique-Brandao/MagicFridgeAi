package com.example.MagicFridgeAI.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Categoria {
    VEGETAL,
    FRUTA,
    CARNE,
    LATICINIO,
    GORDURA,
    DOCE,
    BEBIDA,
    GRAO;

    @JsonCreator
    public static Categoria fromString(String value) {
        return Categoria.valueOf(value.toUpperCase());
    }
}
