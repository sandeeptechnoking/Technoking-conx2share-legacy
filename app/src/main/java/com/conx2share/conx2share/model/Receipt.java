package com.conx2share.conx2share.model;

public class Receipt {

    private String subscriptionId;

    private String packageName;

    private String token;

    public Receipt(String subscriptionId, String packageName, String token) {
        this.subscriptionId = subscriptionId;
        this.packageName = packageName;
        this.token = token;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
