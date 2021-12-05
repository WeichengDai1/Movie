package edu.uci.ics.weiched.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "password", required = true)
    private char[] password;

    @JsonCreator
    public LoginRequestModel(@JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }

    @JsonProperty(value = "email", required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "password", required = true)
    public char[] getPassword() {
        return password;
    }
}
