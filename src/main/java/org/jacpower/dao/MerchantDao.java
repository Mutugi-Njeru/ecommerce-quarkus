package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jacpower.model.Merchant;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class MerchantDao {

    @Inject
    AgroalDataSource ads;

    private static final Logger logger= LoggerFactory.getLogger(MerchantDao.class);

    public  int createMerchant(Merchant merchant){
        String query="INSERT INTO merchant (name, email, address, location) VALUES (?,?,?,?)";
        int merchantId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, merchant.name());
            preparedStatement.setString(2, merchant.email());
            preparedStatement.setString(3, merchant.address());
            preparedStatement.setString(4, merchant.location());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                merchantId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return merchantId;
    }
}
