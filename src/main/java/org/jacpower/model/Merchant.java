package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;

public record Merchant(
        @NotBlank(message = "name cannot be empty")
        String name,
        @NotBlank(message = "email cannot be blank")
        String email,
        @NotBlank(message = "address cannot be empty")
        String address,
        @NotBlank(message = "location cannot be blank")
        String location)
{
    public Merchant (JsonObject object){
        this(
                object.getString("name"),
                object.getString("email"),
                object.getString("address"),
                object.getString("location")
        );
    }
}
