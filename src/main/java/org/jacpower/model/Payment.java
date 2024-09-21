package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;

public record Payment (
        @NotNull(message = "userId cannot be null")
        int userId,
        @NotNull(message = "total amount cannot be null")
        int total)
{
        public Payment(JsonObject object){
                this(
                        object.getInt("userId"),
                        object.getInt("amount")
                );
        }
}
