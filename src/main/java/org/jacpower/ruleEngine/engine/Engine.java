package org.jacpower.ruleEngine.engine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.jacpower.enums.RequestTypes;
import org.jacpower.records.Token;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class Engine {
    @Inject
    JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(Engine.class);

    @Inject
    private Instance<ServiceRule> rules;

    public Response routeRequest(JsonObject request, String module, String bearerToken) {
        String requestType=request.getString("requestType");
        Token token = authorizeUser(requestType, bearerToken);

        if (token.isValid()){
            logger.info("Module received==========>{}", module);
            for (ServiceRule rule : rules) {
                if (rule.matches(module)) {
                    return Response.ok(rule.apply(request).toString()).build();
                }
            }
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unknown request module. Please try again.")
                    .build();
        }
        else return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("invalid token")
                .build();
    }

    private Token authorizeUser(String requestType, String bearerToken){
        String token = (bearerToken.startsWith("Bearer"))
                ? bearerToken.replace("Bearer ", "").trim()
                : bearerToken;

        return (requestType.equalsIgnoreCase(RequestTypes.AUTHENTICATE_USER.name()))
                ? new Token(true, "")
                : jwtService.decodeAccessToken(token);
    }
    
}
