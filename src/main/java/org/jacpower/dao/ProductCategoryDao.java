package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jacpower.model.ProductCategory;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class ProductCategoryDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(ProductCategoryDao.class);

    //should be done by admin
    public boolean checkIsAdmin(int userId){
        String query= """
                SELECT COUNT(u.user_id)
                FROM users u
                INNER JOIN user_categories uc ON uc.user_category_id=u.user_category_id
                WHERE u.user_id=? AND u.is_active=true AND uc.title='Admin'""";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }
    //check if category exists
    public boolean isCategoryExists(String category){
        String query="SELECT count(category_id) FROM product_categories WHERE category=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==0;
    }
    //add category
    public int addProductCategory(ProductCategory categories){
        String query="INSERT INTO product_categories (category) VALUES (?)";
        int categoryId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, categories.category());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                categoryId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return categoryId;
    }
}
