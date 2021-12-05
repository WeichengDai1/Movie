package edu.uci.ics.weiched.service.basic.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MathResponseModel {
    @JsonCreator
    public MathResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                             @JsonProperty(value = "message", required = true) String message,
                             @JsonProperty(value = "value") Integer value) {
        this.resultCode = resultCode;
        this.message = message;
        this.value = value;
    }

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "value")
    private Integer value;

    @JsonProperty("resultCode")
    public int getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("value")
    public Integer getValue(){
        return value;
    }

}
