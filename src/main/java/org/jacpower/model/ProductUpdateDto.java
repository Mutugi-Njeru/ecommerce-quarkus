package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateDto(
        @NotNull(message = "productId cannot be empty")
        int productId,
        @NotBlank(message = "product name cannot be empty")
        String name,
        @NotNull(message = "quantity in stock cannot be empty")
        int inStock,
        @NotNull(message = "price cannot be null")
        int price)
{
    public ProductUpdateDto(JsonObject object){
        this(
                object.getInt("productId"),
                object.getString("name"),
                object.getInt("inStock"),
                object.getInt("price")
        );

    }
}
