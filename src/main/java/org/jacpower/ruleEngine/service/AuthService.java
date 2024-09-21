package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.UserDao;
import org.jacpower.records.Authentication;
import org.jacpower.records.ServiceResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

@ApplicationScoped
public class AuthService {
    @Inject
    JwtService jwtService;
    @Inject
    UserDao userDao;

    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    public ServiceResponder authenticateUser(JsonObject object){
        String username= object.getString("username");
        String password= object.getString("password");
        Authentication authentication=userDao.authenticateUser(username, password);

         if (authentication.isAuthenticated()){
             String accessToken=generateAccessToken(authentication);
             JsonObject response = Json.createObjectBuilder()
                     .add("authentication", Json.createObjectBuilder()
                             .add("bearer", accessToken)
                             .add("type", "token")
                             .add("expiresIn", 3600))
                     .build();
             return new ServiceResponder(Response.Status.OK.getStatusCode(), true, response);
         }
         else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "sorry wrong username or password or account is inactive");
    }

    private String generateAccessToken(Authentication authentication){
        return Base64.getEncoder().encodeToString(jwtService.generateAccessToken(authentication).getBytes());
    }
}

