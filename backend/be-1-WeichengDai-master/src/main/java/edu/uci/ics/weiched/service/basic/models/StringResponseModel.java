package edu.uci.ics.weiched.service.basic.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StringResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "reversed",required = true)
    private String reversed;

    @JsonCreator
    public StringResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                @JsonProperty(value = "message", required = true) String message,
                                @JsonProperty(value = "reversed",required = true) String reversed) {
        this.resultCode = resultCode;
        this.message = message;
        this.reversed = reversed;
    }

    @JsonProperty("resultCode")
    public int getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("reversed")
    public String getReversed(){
        return reversed;
    }
}
