package org.jacpower.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jacpower.model.Product;
import org.jacpower.model.ProductUpdateDto;
import org.jacpower.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class ProductDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(ProductDao.class);

    //does the product exist
    public boolean isProductExist(String name){
        String query="SELECT COUNT(product_id) FROM products WHERE name=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    //add product description
    public int addProductDescription(Product product){
        String query="INSERT INTO product_description (color, warranty, full_description) VALUES (?,?,?)";
        int descriptionId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, product.color());
            preparedStatement.setInt(2, product.warranty());
            preparedStatement.setString(3, product.description());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                descriptionId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return descriptionId;
    }

    //add product
    public int addProduct(int descriptionId, Product product){
        String query="INSERT INTO products (merchant_id, category_id, description_id, name, in_stock, price) VALUES (?,?,?,?,?,?)";
        int productId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, product.merchantId());
            preparedStatement.setInt(2, product.categoryId());
            preparedStatement.setInt(3, descriptionId);
            preparedStatement.setString(4, product.name());
            preparedStatement.setInt(5, product.inStock());
            preparedStatement.setInt(6, product.price());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                productId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return productId;
    }

    //view all products
    public JsonObject getAllProducts (){
        String query= """
                SELECT  p.name, p.price, pd.color, pd.warranty, pd.full_description, p.product_id, p.in_stock\s
                FROM products p
                INNER JOIN product_description pd ON pd.description_id=p.description_id""";
        var products=Json.createArrayBuilder();
        var productsJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                var product=Json.createObjectBuilder()
                        .add("name", resultSet.getString(1))
                        .add("price", resultSet.getInt(2))
                        .add("color", resultSet.getString(3))
                        .add("warranty", resultSet.getInt(4))
                        .add("fullDescription", resultSet.getString(5))
                        .add("productId", resultSet.getInt(6))
                        .add("inStock", resultSet.getInt(7));
                products.add(product);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return productsJson.add("products", products).build();
    }
    //update remaining stock
    public boolean updateRemainingStock(int units, int productId){
        String query="UPDATE products SET in_stock=(in_stock - ?) WHERE product_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, units);
            preparedStatement.setInt(2, productId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }

    //get products by category
    public JsonObject getProductsByCategory(String category){
        String query= """
                SELECT p.product_id, p.name, pc.category, p.in_stock, p.price, pd.full_description
                FROM products p
                INNER JOIN product_categories pc ON p.category_id=pc.category_id
                INNER JOIN product_description pd ON p.description_id=pd.description_id
                WHERE pc.category like ?""";
        String categoryLike= "%" + category + "%";
        var productsJson=Json.createObjectBuilder();
        var productsArray=Json.createArrayBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, categoryLike);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                var product=Json.createObjectBuilder()
                        .add("productId", resultSet.getInt(1))
                        .add("name", resultSet.getString(2))
                        .add("category", resultSet.getString(3))
                        .add("itemsInStock", resultSet.getInt(4))
                        .add("pricePerItem", resultSet.getInt(5))
                        .add("description", resultSet.getString(6));
                productsArray.add(product);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return productsJson.add("products", productsArray).build();
    }
    // update products units
    public boolean updateProductUnits(int units, int productId){
        String query="UPDATE products SET in_stock=? WHERE product_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, units);
            preparedStatement.setInt(2, productId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }

    //update product price
    public boolean updateProductDetails(ProductUpdateDto product){
        String query="UPDATE products SET name=?, in_stock=?, price=? WHERE product_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.name());
            preparedStatement.setInt(2, product.inStock());
            preparedStatement.setInt(3, product.price());
            preparedStatement.setInt(4, product.productId());
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    //search for a product by name
    public JsonObject searchProductByName(String name){
        String query= """
                SELECT p.product_id, p.name, p.in_stock, p.price, pd.full_description
                FROM products p
                INNER JOIN product_description pd ON p.description_id=pd.description_id
                WHERE p.name LIKE ?""";
        var productsJson=Json.createObjectBuilder();
        var productsArray=Json.createArrayBuilder();
        String nameLike='%' +name+ '%';
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameLike);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                var product=Json.createObjectBuilder()
                        .add("productId", resultSet.getInt(1))
                        .add("name", resultSet.getString(2))
                        .add("inStock", resultSet.getInt(3))
                        .add("price", resultSet.getInt(4))
                        .add("description", resultSet.getString(5));
                productsArray.add(product);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return productsJson.add("products", productsArray).build();
    }
    // update product description
   public Boolean updateProductDescription(String description, int descriptionId){
        String query="UPDATE product_description SET full_description=? WHERE description_id=?";
        boolean status=false;
       try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
           preparedStatement.setString(1, description);
           preparedStatement.setInt(2, descriptionId);
           status=preparedStatement.executeUpdate()>0;
       } catch (SQLException ex) {
           logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
       }
       return status;
   }









}
