package edu.uci.ics.weiched.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieModel {
    @JsonProperty(value = "movie_id",required = true)
    private String movie_id;

    @JsonProperty(value = "title", required = true)
    private String title;

    @JsonProperty(value = "year", required = true)
    private int year;

    @JsonProperty(value = "director", required = true)
    private String director;

    @JsonProperty(value = "rating", required = true)
    private float rating;

    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;

    @JsonProperty(value = "poster_path")
    private String poster_path;

    @JsonProperty(value = "hidden")
    private Boolean hidden;

    @JsonCreator
    public MovieModel(@JsonProperty(value = "movie_id", required = true) String movie_id,
                      @JsonProperty(value = "title", required = true) String title,
                      @JsonProperty(value = "year", required = true) int year,
                      @JsonProperty(value = "director", required = true) String director,
                      @JsonProperty(value = "rating", required = true) float rating,
                      @JsonProperty(value = "backdrop_path") String backdrop_path,
                      @JsonProperty(value = "poster_path") String poster_path,
                      @JsonProperty(value = "hidden") Boolean hidden) {
        this.movie_id = movie_id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
        this.hidden = hidden;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    @JsonProperty(value = "hidden")
    public Boolean getHidden() {
        return hidden;
    }

    @JsonProperty(value = "hidden")
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
