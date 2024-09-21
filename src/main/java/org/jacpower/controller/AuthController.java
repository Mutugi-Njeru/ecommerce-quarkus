package org.jacpower.controller;

import jakarta.enterprise.context.ApplicationScoped;
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
import java.util.Base64;

@Path("auth")
@ApplicationScoped
public class AuthController {
    @Inject
    Engine engine;

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(InputStream inputStream, @HeaderParam("Authorization") String basicAuthHeader) {
        var authArray = new String(Base64.getDecoder().decode(basicAuthHeader.replace("Basic", "").trim())).split(":");

        var request = Json.createObjectBuilder()
                .add("username", authArray[0])
                .add("password", authArray[1])
                .add("requestType",RequestTypes.AUTHENTICATE_USER.name());
        return engine.routeRequest(request.build(), Modules.AUTH.name(), "");
    }

}
