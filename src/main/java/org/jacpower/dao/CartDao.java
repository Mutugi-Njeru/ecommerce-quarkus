package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
}


