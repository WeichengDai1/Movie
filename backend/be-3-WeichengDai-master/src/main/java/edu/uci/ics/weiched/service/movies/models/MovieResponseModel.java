package edu.uci.ics.weiched.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.weiched.service.movies.logger.ServiceLogger;
import org.jvnet.hk2.annotations.Optional;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "movies")
    private MovieModel[] movieModels;

    @JsonCreator
    public MovieResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                              @JsonProperty(value = "message", required = true) String message,
                              @JsonProperty(value = "movies") MovieModel[] movieModels) {
        this.resultCode = resultCode;
        this.message = message;
        this.setMovieModels(movieModels);
    }

    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "movies")
    public MovieModel[] getMovieModels() {
//        ServiceLogger.LOGGER.info("this is getter");
        return movieModels;
    }

    @JsonProperty(value = "movies")
    private void setMovieModels(MovieModel[] movieModels) {
//        ServiceLogger.LOGGER.info(movieModels);
        this.movieModels = movieModels;
    }
}
