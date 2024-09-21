package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.model.Cart;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class CartDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(CartDao.class);
    //check if quantity is enough
    public boolean confirmAvailableUnits(int units, int productId){
        String query="SELECT COUNT(product_id) FROM products WHERE in_stock >=? AND product_id=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, units);
            preparedStatement.setInt(2, productId);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    //add product to cart
    public int addToCart(Cart cart){
        String query="INSERT INTO cart_items(product_id, user_id, quantity)VALUES (?,?,?)";
        int cartId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, cart.productId());
                preparedStatement.setInt(2, cart.userId());
                preparedStatement.setInt(3, cart.quantity());
                preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                cartId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return cartId;
    }

    //remove item from cart
    public boolean removeFromCart(int cartId){
        String query="DELETE FROM cart_items WHERE cart_id=?";
        boolean isDeleted=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cartId);
            isDeleted=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return isDeleted;
    }
    //get all cart items and total price
    public JsonObject getAllCartItems(int userId){
        String query= """
                WITH cart_totals AS (
                SELECT p.product_id, p.name, c.cart_id, c.quantity,  pd.full_description,  pd.color,  pd.warranty,  p.price, (p.price * c.quantity) as total_price
                FROM  cart_items c\s
                INNER JOIN  products p ON p.product_id = c.product_id
                INNER JOIN product_description pd ON pd.description_id = p.description_id
                WHERE c.user_id=? AND c.status='cart')
                SELECT ct.*,\s
                (SELECT SUM(total_price) FROM cart_totals) as grand_total
                FROM cart_totals ct""";
        var cartJson= Json.createObjectBuilder();
        var itemsArray=Json.createArrayBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                var item=Json.createObjectBuilder()
                        .add("productId", resultSet.getInt(1))
                        .add("name", resultSet.getString(2))
                        .add("cartId", resultSet.getInt(3))
                        .add("quantity", resultSet.getInt(4))
                        .add("description", resultSet.getString(5))
                        .add("color", resultSet.getString(6))
                        .add("warranty", resultSet.getInt(7))
                        .add("price", resultSet.getInt(8))
                        .add("totalPrice", resultSet.getInt(9))
                        .add("grandTotal", resultSet.getInt(10));
                itemsArray.add(item);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return cartJson.add("cartItems", itemsArray).build();
    }

    //update cart after payment is made
    public boolean updateCartStatus(int userId){
        String query="UPDATE cart_items SET status='paid' WHERE user_id=? AND status='cart'";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }

}


