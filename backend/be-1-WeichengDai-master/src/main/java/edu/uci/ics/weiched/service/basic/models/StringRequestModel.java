package edu.uci.ics.weiched.service.basic.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StringRequestModel {
    @JsonProperty(value = "raw", required = true)
    private String string;

    @JsonCreator
    public StringRequestModel(@JsonProperty(value = "raw", required = true) String string){
        this.string = string;
    }

    @JsonProperty("raw")
    public String getRaw() {
        return string;
    }
}
