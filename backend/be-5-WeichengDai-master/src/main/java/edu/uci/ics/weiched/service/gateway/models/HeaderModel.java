package edu.uci.ics.weiched.service.gateway.models;

import javax.ws.rs.core.HttpHeaders;

public class HeaderModel {
    private String email;
    private String session_id;
    private String transaction_id;

    public HeaderModel(HttpHeaders headers){
        email = headers.getHeaderString("email");
        session_id = headers.getHeaderString("session_id");
        transaction_id = headers.getHeaderString("transaction_id");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
