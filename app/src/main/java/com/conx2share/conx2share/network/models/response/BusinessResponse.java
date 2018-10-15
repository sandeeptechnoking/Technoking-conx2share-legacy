package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Business;

public class BusinessResponse {

    private Business business;

    public BusinessResponse(Business business) {
        this.business = business;
    }

    public Business getBusiness() {
        return business;
    }
}
