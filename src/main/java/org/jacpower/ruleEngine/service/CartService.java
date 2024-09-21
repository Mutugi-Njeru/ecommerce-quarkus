package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.CartDao;
import org.jacpower.model.Cart;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class CartService {
    @Inject
    CartDao cartDao;

    public ServiceResponder addToCart(Cart cart){
        boolean isAvailable= cartDao.confirmAvailableUnits(cart.quantity(), cart.productId());
        if (isAvailable){
            int cartId= cartDao.addToCart(cart);
            return (cartId>0)
                    ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "cart item added successfully")
                    : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot add item to cart");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "unavailable number of units. Please try less units");
    }

    public ServiceResponder removeFromCart(int cartId){
        boolean isRemoved= cartDao.removeFromCart(cartId);
        return (isRemoved)
                ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "item removed successfully")
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot remove item. Please try again");
    }
    public ServiceResponder getAllCartItems(int userId){
        JsonObject cartItems=cartDao.getAllCartItems(userId);
        return (!cartItems.isEmpty())
                ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, cartItems)
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "your cart is empty. Please add items");
    }

}
