package org.jacpower.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record User(
        @NotNull(message = "merchantId cannot be null")
        int merchantId,
        @NotBlank(message = "username cannot be blank")
        String username,
        @NotBlank(message = "password cannot be blank")
        String password,
        @NotBlank(message = "firstname cannot be blank")
        String firstname,
        @NotBlank(message = "lastname cannot be blank")
        String lastname,
        @NotBlank(message = "msisdn cannot be blank")
        @Size(min = 12, message = "msisdn must have 12 digits")
        String msisdn,
        @NotBlank(message = "email cannot be empty")
        String email)
{
    public User (JsonObject object){
        this(
                object.getInt("merchantId"),
                object.getString("username"),
                object.getString("password"),
                object.getString("firstname"),
                object.getString("lastname"),
                object.getString("msisdn"),
                object.getString("email")
        );

    }
}
