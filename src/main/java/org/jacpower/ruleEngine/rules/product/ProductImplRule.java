package org.jacpower.ruleEngine.rules.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.Merchant;
import org.jacpower.model.Product;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.ProductService;
import org.jacpower.utility.Util;

import java.util.List;

@ApplicationScoped
public class ProductImplRule implements ServiceRule {
    @Inject
    ProductService productService;
    @Inject
    BeanValidatorService validatorService;
    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.PRODUCT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)){
            case ADD_PRODUCT:
                Product product=new Product(requestBody);
                List<String> violations = validatorService.validateDTO(product);
                if (violations.isEmpty()){
                    return Util.buildResponse(productService.addProduct(product));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            case GET_ALL_PRODUCTS:
                return Util.buildResponse(productService.getAllProducts());
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
