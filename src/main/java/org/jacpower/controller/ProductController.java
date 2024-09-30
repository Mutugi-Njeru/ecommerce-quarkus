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
    @PUT
    @Path("update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(InputStream inputStream, @PathParam("id") int userId, @HeaderParam("Authorization") String basicAuthHeader) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.UPDATE_PRODUCT_DETAILS.name())
                .add("userId", userId)
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
    @GET
    @Path("category/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductsByCategory(@HeaderParam("Authorization") String basicAuthHeader, @PathParam("name") String name){
        JsonObject requestJson=Json.createObjectBuilder()
                .add("requestType", RequestTypes.GET_PRODUCTS_BY_CATEGORY.name())
                .add("category", name)
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }

    @PUT
    @Path("update/units")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUnits(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson=Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.UPDATE_UNITS.name())
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }
    @GET
    @Path("find/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByName(@PathParam("name")String  name, @HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson=Json.createObjectBuilder()
                .add("requestType", RequestTypes.FIND_PRODUCT_BY_NAME.name())
                .add("name", name)
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }
    @PUT
    @Path("description/{id}")
    public Response updateProductDescription(InputStream inputStream, @PathParam("id") int id, @HeaderParam("Authorization") String basicAuthHeader){
        JsonObject requestJson=Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.UPDATE_PRODUCT_DESCRIPTION.name())
                .add("descriptionId", id)
                .build();
        return engine.routeRequest(requestJson, Modules.PRODUCT.name(), basicAuthHeader);
    }

}
