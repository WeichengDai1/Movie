package edu.uci.ics.weiched.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jvnet.hk2.annotations.Optional;

public class IdmResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    public int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "session_id")
    @Optional
    public String session_id;

    public IdmResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                            @JsonProperty(value = "message", required = true) String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "session_id")
    public void setSession_id(String session_id){
        this.session_id = session_id;
    }
}
