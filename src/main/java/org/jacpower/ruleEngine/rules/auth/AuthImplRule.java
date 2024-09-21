package org.jacpower.ruleEngine.rules.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.AuthService;
import org.jacpower.utility.Util;

@ApplicationScoped
public class AuthImplRule implements ServiceRule {
    @Inject
    AuthService authService;

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.AUTH.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)){
            case AUTHENTICATE_USER:
                return  Util.buildResponse(authService.authenticateUser(requestBody));
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
