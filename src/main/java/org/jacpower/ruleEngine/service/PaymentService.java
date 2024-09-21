package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.CartDao;
import org.jacpower.dao.PaymentDao;
import org.jacpower.dao.ProductDao;
import org.jacpower.model.Payment;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class PaymentService {
    @Inject
    CartDao cartDao;
    @Inject
    PaymentDao paymentDao;
    @Inject
    ProductDao productDao;

    public ServiceResponder addPayment(Payment payment){
        //confirm if paying total amount
        int totalAmount= paymentDao.getTotalAmount(payment.userId());
        if (payment.total()==totalAmount){
            JsonObject allCartItems = cartDao.getAllCartItems(payment.userId());
            JsonArray cartItems=allCartItems.getJsonArray("cartItems");
            boolean isUpdated=false;

            for (int i = 0; i < cartItems.size(); i++){
                JsonObject cartItem = cartItems.getJsonObject(i);
                int units= cartItem.getInt("quantity");
                int productId=cartItem.getInt("productId");
                isUpdated= productDao.updateRemainingStock(units, productId);
            }
            if (isUpdated){
                int paymentId = paymentDao.addPayment(payment);
                boolean updated = cartDao.updateCartStatus(payment.userId());
                return (paymentId>0 && updated)
                        ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "purchase successful")
                        : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot purchase item(s)");
            }
            else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "failed to update stocks");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "Please pay the exact amount");
    }
}
