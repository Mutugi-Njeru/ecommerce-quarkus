package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;

public record Cart (
        @NotNull(message = "productId cannot be null")
        int productId,
        @NotNull(message = "userId cannot be null")
        int userId,
        @NotNull(message = "quantity cannot be null")
        int quantity
) {
    public Cart(JsonObject object){
        this(
                object.getInt("productId"),
                object.getInt("userId"),
                object.getInt("quantity")
        );
    }
}
