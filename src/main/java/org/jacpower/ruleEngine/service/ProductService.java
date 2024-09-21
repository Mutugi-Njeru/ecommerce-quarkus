package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import netscape.javascript.JSObject;
import org.jacpower.dao.ProductCategoryDao;
import org.jacpower.dao.ProductDao;
import org.jacpower.model.Product;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductDao productDao;
    @Inject
    ProductCategoryDao categoryDao;

    public ServiceResponder addProduct(Product product){
        // should be added by admin
        if (categoryDao.checkIsAdmin(product.userID())){
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

}
