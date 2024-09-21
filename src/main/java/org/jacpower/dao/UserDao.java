package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jacpower.ciphers.Sha256Hasher;
import org.jacpower.model.User;
import org.jacpower.records.Authentication;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class UserDao {
    @Inject
    AgroalDataSource ads;
    @Inject
    Sha256Hasher hasher;
    private static final Logger logger= LoggerFactory.getLogger(UserDao.class);

    //check if user exists
    public boolean isUserExists(String email, String username){
        boolean status = false;
        String query= """
                SELECT COUNT(u.user_id)
                FROM user_details ud
                INNER JOIN users u ON u.user_id=ud.user_id
                WHERE ud.email=? OR u.username=?""";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, username);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                status=resultSet.getInt(1)>0;
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    //create user
    public int createUser(User user, int userCategory){
        String query="INSERT INTO users (merchant_id, user_category_id, username, password, is_active) VALUES (?,?,?,?,?)";
        String hashPassword= hasher.createHashText(user.password());
        int userId = 0;

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, user.merchantId());
            preparedStatement.setInt(2, userCategory);
            preparedStatement.setString(3, user.username());
            preparedStatement.setString(4, hashPassword);
            preparedStatement.setBoolean(5, true);
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                userId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userId;
    }
    //add user details
    public int addUserDetails(int userId, User user){
        String query="INSERT INTO user_details (user_id, first_name, last_name, msisdn, email) VALUES (?,?,?,?,?)";
        int userDetailsId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, user.firstname());
            preparedStatement.setString(3, user.lastname());
            preparedStatement.setString(4, user.msisdn());
            preparedStatement.setString(5, user.email());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                userDetailsId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userDetailsId;
    }

    //handle auth
    public Authentication authenticateUser(String username, String password){
        String query= "SELECT COUNT(u.user_id) from users u WHERE u.username = ? AND u.password = ? AND is_active=true";
        int count=0;

        String hashPassword= hasher.createHashText(password);
        try (Connection connection=ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashPassword);
            ResultSet resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return new Authentication(count==1, username);
    }
    //add userId holder
    public int addUserIdHolder(int userId){
        String query="INSERT INTO id_holder (user_id) VALUES (?)";
        int holderId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                holderId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return holderId;
    }
}
