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

@Path("product")
public class ProductController {
    @Inject
    Engine engine;

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.ADD_PRODUCT.name())
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts(@HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson=Json.createObjectBuilder()
                .add("requestType", RequestTypes.GET_ALL_PRODUCTS.name())
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }
}
