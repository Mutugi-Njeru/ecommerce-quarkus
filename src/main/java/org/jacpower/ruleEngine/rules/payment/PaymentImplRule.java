package org.jacpower.ruleEngine.rules.payment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.enums.Modules;
import org.jacpower.enums.RequestTypes;
import org.jacpower.model.Merchant;
import org.jacpower.model.Payment;
import org.jacpower.ruleEngine.interfaces.ServiceRule;
import org.jacpower.ruleEngine.service.BeanValidatorService;
import org.jacpower.ruleEngine.service.PaymentService;
import org.jacpower.utility.Util;

import java.util.List;

@ApplicationScoped
public class PaymentImplRule implements ServiceRule {
    @Inject
    PaymentService paymentService;
    @Inject
    BeanValidatorService validatorService;
    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.PAYMENT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");


        switch (RequestTypes.valueOf(requestType)){
            case ADD_PAYMENT:
                Payment payment= new Payment(requestBody);
                List<String> violations = validatorService.validateDTO(payment);
                if (violations.isEmpty()){
                    return Util.buildResponse(paymentService.addPayment(payment));
                }
                else return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
