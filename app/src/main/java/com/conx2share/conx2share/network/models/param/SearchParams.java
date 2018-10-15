package com.conx2share.conx2share.network.models.param;

public class SearchParams {

    private int page;

    private String query;

    public SearchParams(String query, int page) {
        this.query = query;
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
