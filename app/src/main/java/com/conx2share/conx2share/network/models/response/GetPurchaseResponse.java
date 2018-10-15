package com.conx2share.conx2share.network.models.response;

import java.util.Date;

public class GetPurchaseResponse {

    private Integer id;

    private String name;

    private String storageLimit;

    private Date createdAt;

    private Date updatedAt;

    public GetPurchaseResponse(Integer id, String name, String storageLimit, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.storageLimit = storageLimit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(String storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
