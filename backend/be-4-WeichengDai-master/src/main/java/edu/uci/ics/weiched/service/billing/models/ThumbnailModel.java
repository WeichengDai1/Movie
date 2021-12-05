package edu.uci.ics.weiched.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailModel {
    @JsonProperty(value = "movie_id",required = true)
    private String movie_id;

    @JsonProperty(value = "title",required = true)
    private String title;

    @JsonProperty(value = "backdrop_path",required = true)
    private String backdrop_path;

    @JsonProperty(value = "poster_path",required = true)
    private String poster_path;

    @JsonCreator
    public ThumbnailModel(@JsonProperty(value = "movie_id",required = true)String movie_id,
                          @JsonProperty(value = "title",required = true)String title,
                          @JsonProperty(value = "backdrop_path",required = true)String backdrop_path,
                          @JsonProperty(value = "poster_path",required = true)String poster_path) {
        this.movie_id = movie_id;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
    }

    @JsonProperty(value = "movie_id",required = true)
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "movie_id",required = true)
    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    @JsonProperty(value = "title",required = true)
    public String getTitle() {
        return title;
    }

    @JsonProperty(value = "title",required = true)
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty(value = "backdrop_path",required = true)
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty(value = "backdrop_path",required = true)
    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    @JsonProperty(value = "poster_path",required = true)
    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty(value = "poster_path",required = true)
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}
