package org.jacpower.ruleEngine.rules.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.Merchant;
import org.jacpower.model.User;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.UserService;
import org.jacpower.utility.Util;

import java.util.List;

@ApplicationScoped
public class UserImplRule implements ServiceRule {
    @Inject
    UserService userService;
    @Inject
    BeanValidatorService validatorService;
    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.USER.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");


        switch (RequestTypes.valueOf(requestType)){
            case CREATE_ADMIN:
                User user =new User(requestBody);
                List<String> violations = validatorService.validateDTO(user);
                if (violations.isEmpty()){
                    return Util.buildResponse(userService.createAdmin(user));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            case CREATE_CUSTOMER:
                User user1 =new User(requestBody);
                List<String> violations1 = validatorService.validateDTO(user1);
                if (violations1.isEmpty()){
                    return Util.buildResponse(userService.createCustomer(user1));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations1)).build();
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }

    }
}
