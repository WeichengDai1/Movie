package edu.uci.ics.weiched.service.movies.resources;

import edu.uci.ics.weiched.service.movies.logger.ServiceLogger;
import edu.uci.ics.weiched.service.movies.models.MovieModel;
import edu.uci.ics.weiched.service.movies.models.MovieResponseModel;
import edu.uci.ics.weiched.service.movies.util.Utility;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

@Path("/")
public class MovieBrowseResource {
    @Path("browse/{phrase}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response browseMovies(@Context HttpHeaders headers,
                                 @PathParam("phrase") String phrase,
                                 @DefaultValue("10")@QueryParam("limit") Integer limit,
                                 @DefaultValue("0")@QueryParam("offset") Integer offset,
                                 @DefaultValue("title")@QueryParam("orderby") String orderby,
                                 @DefaultValue("ASC")@QueryParam("direction") String direction) {
        System.out.println(phrase);
        String[] phrases = phrase.split(",");
        String query = "SELECT JSON_ARRAYAGG(JSON_OBJECT('movie_id', t.movie_id, 'title', t.title, 'year', t.year, 'director', t.name, 'rating', t.rating, 'backdrop_path', t.backdrop_path, 'poster_path', t.poster_path, 'hidden', t.hidden)) AS Movie " +
                "FROM (SELECT DISTINCT m.movie_id ,m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                "FROM movie AS m LEFT OUTER JOIN person AS p ON m.director_id = p.person_id ";
        for(int i = 1; i <= phrases.length; i++) {
            query += " LEFT OUTER JOIN keyword_in_movie AS km" + i + " ON m.movie_id = km" + i +".movie_id " +
                    "  LEFT OUTER JOIN keyword AS k" + i + " ON km" + i + ".keyword_id = k" + i + ".keyword_id ";
        }
        query += " WHERE TRUE ";
        for(int i = 1; i <= phrases.length; i++) {
            query += " AND k" + i + ".name LIKE ? ";
        }
        if (!Utility.getPlevel(headers.getHeaderString("email"), 4))
            query += " AND m.hidden = 0";
        if(direction == null) {
            direction = " ASC ";
        }
        if(orderby == null) {
            orderby = "title";
        }
        switch (orderby) {
            case "title":
                query += " ORDER BY m.title " + direction + ", m.rating DESC";
                break;
            case "rating":
                query += " ORDER BY m.rating " + direction + ", m.title ASC";
                break;
            case "year":
                query += " ORDER BY m.year " + direction + ", m.rating DESC";
                break;
            default:
                query += " ORDER BY m.title " + direction + ", m.rating DESC";
                break;
        }
        query += " LIMIT ? OFFSET ? ";
        query +=  ") AS t";
        ArrayList<Object> p = createBrowseParameters(phrase,limit,offset);
        MovieModel[] movies = null;
        try {
            ResultSet rs = Utility.preparedStatement(query, p).executeQuery();
            if(rs.next()) {
                movies = Utility.mapping(rs.getString("Movie"), MovieModel[].class);
                if(movies == null||movies.length==0) {
                    ServiceLogger.LOGGER.info("No movies found.");
                    MovieResponseModel movieResponseModel = new MovieResponseModel(211, "No movies found with search parameters.",null);
                    Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(movieResponseModel);
                    builder.header("email",headers.getHeaderString("email"));
                    builder.header("session_id",headers.getHeaderString("session_id"));
                    builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                    return builder.build();
                }
                else {
                    if (!Utility.getPlevel(headers.getHeaderString("email"), 4))
                        for (MovieModel m : movies)
                            m.setHidden(null);
                    ServiceLogger.LOGGER.info("Movies found.");
                }
            }
        } catch (SQLException e) {
            MovieResponseModel movieResponseModel = new MovieResponseModel(-1, " Internal server error.",null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(movieResponseModel).build();
        }
        ServiceLogger.LOGGER.info("movies found, return to the client");
        MovieResponseModel responseModel = new MovieResponseModel(210, "Found movie(s) with search parameters.",movies);
//        responseModel.setMovieModels(movies);
        Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id",headers.getHeaderString("transaction_id"));
        return builder.build();
    }

    private ArrayList<Object> createBrowseParameters(String phrase, Integer limit, Integer offset) {
        ArrayList<Object> p = new ArrayList<>();
        String[] phrases = phrase.split(",");
        Collections.addAll(p, phrases);
        if(limit == null || limit != 10 && limit != 25 && limit != 50 && limit != 100)
            limit = 10;
        if(offset == null || offset % limit != 0)
            offset = 0;
        p.add(limit);
        p.add(offset);
        return p;
    }
}
