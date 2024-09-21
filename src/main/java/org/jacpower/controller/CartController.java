package org.jacpower.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
