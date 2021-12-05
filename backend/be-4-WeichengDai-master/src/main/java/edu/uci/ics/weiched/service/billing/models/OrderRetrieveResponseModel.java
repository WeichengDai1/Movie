package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRetrieveResponseModel {
    @JsonProperty(value = "resultCode",required = true)
    private int resultCode;

    @JsonProperty(value = "message",required = true)
    private String message;

    @JsonProperty(value = "transactions")
    private TransactionModel[] transactionModels;

    @JsonCreator
    public OrderRetrieveResponseModel(@JsonProperty(value = "resultCode",required = true) int resultCode,
                                      @JsonProperty(value = "message",required = true) String message,
                                      @JsonProperty(value = "transactions") TransactionModel[] transactionModels){
        this.resultCode=resultCode;
        this.message = message;
        this.transactionModels = transactionModels;
    }

    @JsonProperty(value = "resultCode",required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "resultCode",required = true)
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty(value = "message",required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "message",required = true)
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty(value = "transactions")
    public TransactionModel[] getTransactionModels() {
        return transactionModels;
    }

    @JsonProperty(value = "transactions")
    public void setTransactionModels(TransactionModel[] transactionModels) {
        this.transactionModels = transactionModels;
    }
}
