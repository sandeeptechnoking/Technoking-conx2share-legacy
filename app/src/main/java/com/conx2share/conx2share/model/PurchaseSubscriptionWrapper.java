package com.conx2share.conx2share.model;

public class PurchaseSubscriptionWrapper {

    private Receipt receipt;

    private boolean newApi; // TODO - remove this which was for testing on production

    public PurchaseSubscriptionWrapper(Receipt receipt, boolean newApi) {
        this.receipt = receipt;
        this.newApi = newApi;
    }

    public PurchaseSubscriptionWrapper(Receipt receipt) {
        this.receipt = receipt;
    }

    public boolean isNewApi() {
        return newApi;
    }

    public void setNewApi(boolean newApi) {
        this.newApi = newApi;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}
