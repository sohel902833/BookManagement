package com.sohel.bookmanagement.Model;

public class AccessToken {
    String agentId;
    String token;
    String userId;
    boolean sealed;

    public AccessToken(){

    }

    public AccessToken(String agentId,String token, String userId, boolean sealed) {
        this.agentId=agentId;
        this.token = token;
        this.userId = userId;
        this.sealed = sealed;
    }


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }
}
