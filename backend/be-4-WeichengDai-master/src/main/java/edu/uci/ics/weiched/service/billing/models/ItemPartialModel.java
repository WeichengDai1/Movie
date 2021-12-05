package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemPartialModel {

    @JsonProperty(value = "email",required = true)
    private String email;

    @JsonProperty(value = "unit_price",required = true)
    private float unit_price;

    @JsonProperty(value = "discount",required = true)
    private float discount;

    @JsonProperty(value = "quantity",required = true)
    private int quantity;

    @JsonProperty(value = "movie_id",required = true)
    private String movie_id;

    @JsonCreator
    public ItemPartialModel(@JsonProperty(value = "email",required = true) String email,
                     @JsonProperty(value = "unit_price",required = true) float unit_price,
                     @JsonProperty(value = "discount",required = true) float discount,
                     @JsonProperty(value = "quantity",required = true) int quantity,
                     @JsonProperty(value = "movie_id",required = true) String movie_id){
        this.email=email;
        this.unit_price = unit_price;
        this.discount = discount;
        this.quantity = quantity;
        this.movie_id=movie_id;
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "email",required = true)
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(value = "unit_price",required = true)
    public float getUnit_price() {
        return unit_price;
    }

    @JsonProperty(value = "unit_price",required = true)
    public void setUnit_price(float unit_price) {
        this.unit_price = unit_price;
    }

    @JsonProperty(value = "discount",required = true)
    public float getDiscount() {
        return discount;
    }

    @JsonProperty(value = "discount",required = true)
    public void setDiscount(float discount) {
        this.discount = discount;
    }

    @JsonProperty(value = "quantity",required = true)
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty(value = "quantity",required = true)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonProperty(value = "movie_id",required = true)
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "movie_id",required = true)
    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }
}
