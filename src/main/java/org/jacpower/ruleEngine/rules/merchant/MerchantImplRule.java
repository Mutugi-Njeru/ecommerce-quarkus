package org.jacpower.ruleEngine.rules.merchant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.dao.MerchantDao;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.Merchant;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.MerchantService;
import org.jacpower.utility.Util;

import java.io.StringReader;
import java.util.List;

@ApplicationScoped
public class MerchantImplRule implements ServiceRule {
    @Inject
    MerchantService merchantService;
    @Inject
    BeanValidatorService validatorService;
    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.MERCHANT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");


        switch (RequestTypes.valueOf(requestType)){
            case CREATE_MERCHANT:
                Merchant merchant=new Merchant(requestBody);
                List<String> violations = validatorService.validateDTO(merchant);
                if (violations.isEmpty()){
                    return Util.buildResponse(merchantService.createMerchant(merchant));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
