package org.jacpower.ruleEngine.rules.cart;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.Cart;
import org.jacpower.model.Merchant;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.CartService;
import org.jacpower.utility.Util;

import java.util.List;

@ApplicationScoped
public class CartImplRule implements ServiceRule {
    @Inject
    CartService cartService;
    @Inject
    BeanValidatorService validatorService;

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.CART.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)){
            case ADD_TO_CART:
                Cart cart=new Cart(requestBody);
                List<String> violations = validatorService.validateDTO(cart);
                if (violations.isEmpty()){
                    return Util.buildResponse(cartService.addToCart(cart));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
