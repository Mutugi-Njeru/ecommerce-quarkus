package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCategory(
        @NotBlank(message = "product category cannot be blank")
        String category,
        @NotNull(message = "userId cannot be empty")
        int userId
        )
{
    public ProductCategory(JsonObject object){
        this(
                object.getString("category"),
                object.getInt("userId")
        );
    }
}
