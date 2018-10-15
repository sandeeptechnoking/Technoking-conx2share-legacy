package com.conx2share.conx2share.model;

public class SearchFollowUsers {

    private String query;

    private String groupId;

    private String page;

    public SearchFollowUsers(String query, String groupId, String page) {
        this.query = query;
        this.groupId = groupId;
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
