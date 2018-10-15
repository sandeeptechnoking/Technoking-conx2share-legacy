package com.conx2share.conx2share.model;

public class PromoCodeWrapper {

    private Integer id;

    private PromoCodeHolder user;

    public PromoCodeWrapper(Integer id, PromoCodeHolder user) {
        this.id = id;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PromoCodeHolder getUser() {
        return user;
    }

    public void setUser(PromoCodeHolder user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PromoCodeWrapper{" +
                "id='" + id + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
