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
@Path("user")
public class UserController {
    @Inject
    Engine engine;

    @POST
    @Path("create/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdmin(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.CREATE_ADMIN.name())
                .build();
        return engine.routeRequest(requestJson, Modules.USER.name(), basicAuthHeader);
    }

    @POST
    @Path("create/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.CREATE_CUSTOMER.name())
                .build();
        return engine.routeRequest(requestJson, Modules.USER.name(), basicAuthHeader);
    }
}
