package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.ProductCategoryDao;
import org.jacpower.model.ProductCategory;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class ProductCategoryService {
    @Inject
    ProductCategoryDao categoryDao;

    public ServiceResponder addCategory(ProductCategory categories){
        boolean isAdmin= categoryDao.checkIsAdmin(categories.userId());
        if (isAdmin){
            boolean isCategoryExists= categoryDao.isCategoryExists(categories.category());
            if (isCategoryExists){
                int categoryId= categoryDao.addProductCategory(categories);
                return (categoryId>0)
                        ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "category added successfully")
                        : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot add category");
            }
            else return new ServiceResponder(Response.Status.BAD_REQUEST.getStatusCode(), false, "Sorry, category already exists");

        }
        else return new ServiceResponder(Response.Status.UNAUTHORIZED.getStatusCode(), false, "you are not authorized to perform this operation");

    }
}
