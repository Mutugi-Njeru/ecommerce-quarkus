package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.UserDao;
import org.jacpower.model.User;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class UserService {
    @Inject
    UserDao userDao;

    public ServiceResponder createAdmin(User user){
        boolean isExists= userDao.isUserExists(user.email(), user.username());
        if (!isExists){
            int userId= userDao.createUser(user, 1); //admin
            int userDetailsId=userDao.addUserDetails(userId, user);
            return (userId>0 && userDetailsId>0)
                    ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "admin created successfully")
                    : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create user");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "username or email already taken");
    }

    public ServiceResponder createCustomer(User user){
        boolean isExists= userDao.isUserExists(user.email(), user.username());
        if (!isExists){
            int userId= userDao.createUser(user, 2); //customer
            int holderId=userDao.addUserIdHolder(userId);
            int userDetailsId=userDao.addUserDetails(userId, user);
            return (userId>0 && userDetailsId>0 && holderId>0)
                    ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "user created successfully")
                    : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create user");
        }
        else return new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "username or email already taken");
    }
}
