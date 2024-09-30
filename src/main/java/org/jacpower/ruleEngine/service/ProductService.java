package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.ProductCategoryDao;
import org.jacpower.dao.ProductDao;
import org.jacpower.model.Product;
import org.jacpower.model.ProductUpdateDto;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductDao productDao;
    @Inject
    ProductCategoryDao categoryDao;

    public ServiceResponder addProduct(Product product){
        // should be added by admin
        if (categoryDao.checkIsAdmin(product.userId())){
            if (!productDao.isProductExist(product.name())){
                int descriptionId= productDao.addProductDescription(product);
                int productId=productDao.addProduct(descriptionId, product);
                return (descriptionId>0 && productId>0)
                        ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "product added successfully")
                        : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot add product");
            }
            else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "product already exists with that name");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "you are not authorized to perform this operation");
    }
    public ServiceResponder getAllProducts(){
        return new ServiceResponder(Response.Status.OK.getStatusCode(), true, productDao.getAllProducts());
    }

    public ServiceResponder getProductsByCategory(String category){
        JsonObject products=productDao.getProductsByCategory(category);
        return (!products.isEmpty())
                ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, products)
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "no products found with that category");
    }
    //update units
    public ServiceResponder updateProductUnits(JsonObject object){
        int userId= object.getInt("userId");
        int units= object.getInt("units");
        int productId= object.getInt("productId");
        //should be done by admin
        if (categoryDao.checkIsAdmin(userId)){
            boolean isUpdated = productDao.updateProductUnits(units, productId);
            return (isUpdated)
                    ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "units updated successfully")
                    : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "failed to update units");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "you have no rights to this operation");
    }
    public ServiceResponder updateProduct(int userId, ProductUpdateDto product){
        if (categoryDao.checkIsAdmin(userId)){
            boolean isUpdated= productDao.updateProductDetails(product);
            return (isUpdated)
                    ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "details updated successfully")
                    : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "failed to update units");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "you have no rights to this operation");
    }
    public ServiceResponder findProductByName(String name){
        JsonObject products=productDao.searchProductByName(name);
        return (!products.isEmpty())
                ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, products)
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "No products found with that name");
    }

    public ServiceResponder updateProductDescription(JsonObject object){
        String description= object.getString("description");
        int descriptionId=object.getInt("descriptionId");
        Boolean isUpdated = productDao.updateProductDescription(description, descriptionId);
        return (isUpdated)
                ? new ServiceResponder(Response.Status.ACCEPTED.getStatusCode(), true, "description updated successfully")
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot update description");
    }

}
