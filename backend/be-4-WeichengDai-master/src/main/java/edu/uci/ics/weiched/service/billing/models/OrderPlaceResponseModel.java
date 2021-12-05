package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPlaceResponseModel {
    @JsonProperty(value = "resultCode",required = true)
    private int resultCode;

    @JsonProperty(value = "message",required = true)
    private String message;

    @JsonProperty(value = "approve_url")
    private String approve_url;

    @JsonProperty(value = "token")
    private String token;

    @JsonCreator
    public OrderPlaceResponseModel(@JsonProperty(value = "resultCode",required = true)int resultCode,
                                   @JsonProperty(value = "message",required = true)String message,
                                   @JsonProperty(value = "approve_url")String approve_url,
                                   @JsonProperty(value = "token")String token){
        this.resultCode = resultCode;
        this.message = message;
        this.approve_url = approve_url;
        this.token = token;
    }

    @JsonProperty(value = "resultCode",required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "resultCode",required = true)
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty(value = "message",required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "message",required = true)
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty(value = "approve_url")
    public String getApprove_url() {
        return approve_url;
    }

    @JsonProperty(value = "approve_url")
    public void setApprove_url(String approve_url) {
        this.approve_url = approve_url;
    }

    @JsonProperty(value = "token")
    public String getToken() {
        return token;
    }

    @JsonProperty(value = "token")
    public void setToken(String token) {
        this.token = token;
    }
}
