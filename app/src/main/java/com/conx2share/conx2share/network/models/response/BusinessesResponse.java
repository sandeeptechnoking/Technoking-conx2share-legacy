package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Business;

import java.util.ArrayList;

public class BusinessesResponse {

    public static final String TAG = BusinessesResponse.class.getSimpleName();

    ArrayList<Business> businesses;

    public BusinessesResponse() {
        // NOOP
    }

    public BusinessesResponse(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }
}
