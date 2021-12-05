package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "thumbnails", required = true)
    private ThumbnailModel[] thumbnails;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "thumbnails", required = true)ThumbnailModel[] thumbnails){
        this.resultCode = resultCode;
        this.message = message;
        if(thumbnails == null || thumbnails.length == 0) {
            this.thumbnails = null;
        }else {
            this.thumbnails = thumbnails;
        }
    }

    @JsonProperty("thumbnails")
    public ThumbnailModel[] getThumbnails() {
        return thumbnails;
    }

    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "resultCode", required = true)
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "message", required = true)
    public void setMessage(String message) {
        this.message = message;
    }
}
