package org.jacpower.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jacpower.dao.MerchantDao;
import org.jacpower.model.Merchant;
import org.jacpower.records.ServiceResponder;

@ApplicationScoped
public class MerchantService {

    @Inject
    MerchantDao merchantDao;

    public ServiceResponder createMerchant(Merchant merchant){
        int merchantId= merchantDao.createMerchant(merchant);
        return (merchantId>0)
                ? new ServiceResponder(Response.Status.OK.getStatusCode(), true, "merchant created successfully")
                : new ServiceResponder(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create merchant");
    }
}
