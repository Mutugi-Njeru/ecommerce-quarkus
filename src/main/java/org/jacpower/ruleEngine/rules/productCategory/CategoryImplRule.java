package org.jacpower.ruleEngine.rules.productCategory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.ProductCategory;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.ProductCategoryService;
import org.jacpower.utility.Util;

import java.util.List;

@ApplicationScoped
public class CategoryImplRule implements ServiceRule {
    @Inject
    ProductCategoryService categoryService;
    @Inject
    BeanValidatorService validatorService;

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.CATEGORY.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");


        switch (RequestTypes.valueOf(requestType)){
            case ADD_CATEGORY:
                ProductCategory categories=new ProductCategory(requestBody);
                List<String> violations = validatorService.validateDTO(categories);
                if (violations.isEmpty()){
                    return Util.buildResponse(categoryService.addCategory(categories));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }


    }
}
