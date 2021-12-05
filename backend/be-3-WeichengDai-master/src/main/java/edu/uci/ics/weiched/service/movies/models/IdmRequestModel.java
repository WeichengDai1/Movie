package edu.uci.ics.weiched.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdmRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "session_id", required = true)
    private String session_id;

    public String getTransaction_id() {
        return transaction_id;
    }

    @JsonProperty(value = "transaction_id")
    private String transaction_id;

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    @JsonCreator
    public IdmRequestModel(@JsonProperty(value = "email", required = true) String email,
                           @JsonProperty(value = "session_id", required = true) String session_id,
                           @JsonProperty(value = "transaction_id", required = true) String transaction_id) {
        this.email = email;
        this.session_id = session_id;
        this.transaction_id = transaction_id;
    }

    @JsonProperty(value = "email", required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "session_id", required = true)
    public String getSession_id() {
        return session_id;
    }
}
