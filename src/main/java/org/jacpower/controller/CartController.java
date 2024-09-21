package org.jacpower.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.ruleEngine.engine.Engine;
import org.jacpower.utility.Util;

import java.io.InputStream;

@Path("cart")
public class CartController {
    @Inject
    Engine engine;

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.ADD_TO_CART.name())
                .build();
        return engine.routeRequest(requestJson, Modules.CART.name(), basicAuthHeader);
    }
    @DELETE
    @Path("delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFromCart(@PathParam ("id") int id, @HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson = Json.createObjectBuilder()
                .add("cartId", id)
                .add("requestType", RequestTypes.REMOVE_FROM_CART.name())
                .build();
        return engine.routeRequest(requestJson, Modules.CART.name(), basicAuthHeader);
    }

    @GET
    @Path("items/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCartItems (@PathParam("id") int userId, @HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson = Json.createObjectBuilder()
                .add("userId", userId)
                .add("requestType", RequestTypes.GET_CART_ITEMS.name())
                .build();
        return engine.routeRequest(requestJson, Modules.CART.name(), basicAuthHeader);


    }
}
