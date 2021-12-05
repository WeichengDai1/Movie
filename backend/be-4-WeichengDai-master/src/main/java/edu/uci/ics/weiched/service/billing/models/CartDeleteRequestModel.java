package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartDeleteRequestModel {
    @JsonProperty(value = "email",required = true)
    private String email;

    @JsonProperty(value = "movie_id",required = true)
    private String movie_id;

    @JsonCreator
    public CartDeleteRequestModel(@JsonProperty(value = "email",required = true)String email,
                                  @JsonProperty(value = "movie_id",required = true)String movie_id){
        this.email=email;
        this.movie_id=movie_id;
    }

    @JsonProperty(value = "movie_id",required = true)
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "movie_id",required = true)
    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "email",required = true)
    public void setEmail(String email) {
        this.email = email;
    }
}
