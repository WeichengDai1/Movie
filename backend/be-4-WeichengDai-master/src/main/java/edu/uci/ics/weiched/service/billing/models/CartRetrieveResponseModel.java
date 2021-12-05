package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "items")
    private ItemModel[] itemModels;

    @JsonCreator
    public CartRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message,
                                     @JsonProperty(value = "items") ItemModel[] itemModels){
        this.resultCode = resultCode;
        this.message = message;
        this.itemModels = itemModels;
    }

    @JsonCreator
    public CartRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message){
        this.resultCode = resultCode;
        this.message = message;
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

    @JsonProperty(value = "items")
    public ItemModel[] getItemModels() {
        return itemModels;
    }

    @JsonProperty(value = "items")
    public void setItemModels(ItemModel[] itemModels) {
        this.itemModels = itemModels;
    }
}
