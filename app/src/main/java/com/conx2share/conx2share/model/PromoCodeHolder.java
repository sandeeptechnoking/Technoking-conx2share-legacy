package com.conx2share.conx2share.model;

public class PromoCodeHolder {

    private String promoCode;

    public PromoCodeHolder(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @Override
    public String toString() {
        return "PromoCodeHolder{" +
                "promoCode='" + promoCode + '\'' +
                '}';
    }
}
