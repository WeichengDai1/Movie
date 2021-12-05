package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartRetrieveRequestModel {
    @JsonProperty(value = "email",required = true)
    private String email;

    @JsonCreator
    public CartRetrieveRequestModel(@JsonProperty(value = "email",required = true) String email){
        this.email=email;
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "email",required = true)
    public void setEmail(String email) {
        this.email = email;
    }
}
