package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jacpower.model.Payment;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class PaymentDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(PaymentDao.class);

    public int getTotalAmount(int userId){
        String query= """
                SELECT SUM(c.quantity * p.price) as total_price
                FROM cart_items c
                INNER JOIN products p ON p.product_id=c.product_id
                WHERE c.user_id=? AND c.status='cart'""";
        int totalAmount=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                totalAmount=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return totalAmount;
    }

    //add payment
    public int addPayment(Payment payment){
        String query="INSERT INTO payments (user_id, total) VALUES (?,?)";
        int paymentId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, payment.userId());
            preparedStatement.setInt(2, payment.total());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                paymentId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return paymentId;

    }


}
