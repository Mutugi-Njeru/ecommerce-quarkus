package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Product(
        @NotNull(message = "userId cannot be null")
        int userId,
        @NotNull(message = "merchantId cannot be null")
        int merchantId,
        @NotNull(message = "categoryId cannot be null")
        int categoryId,
        @NotBlank(message = "product name cannot be empty")
        String name,
        @NotNull(message = "quantity in stock cannot be empty")
        int inStock,
        @NotBlank(message = "color cannot be empty")
        String color,
        int warranty,
        @NotBlank(message = "product description cannot be empty")
        String description,
        @NotNull(message = "price cannot be null")
        int price
        )
{
    public Product (JsonObject object){
        this(
                object.getInt("userId"),
                object.getInt("merchantId"),
                object.getInt("categoryId"),
                object.getString("name"),
                object.getInt("inStock"),
                object.getString("color"),
                object.getInt("warranty"),
                object.getString("description"),
                object.getInt("price")
        );

    }
}
