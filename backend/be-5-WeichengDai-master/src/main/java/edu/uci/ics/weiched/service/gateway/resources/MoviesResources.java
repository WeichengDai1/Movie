package edu.uci.ics.weiched.service.gateway.resources;

import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.configs.MoviesConfigs;
import edu.uci.ics.weiched.service.gateway.util.Utility;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/movies")
public class MoviesResources {
    @Path("/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers,
                           @QueryParam("title") String title,
                           @QueryParam("year") Integer year,
                           @QueryParam("director") String director,
                           @QueryParam("genre") String genre,
                           @QueryParam("hidden") Boolean hidden,
                           @DefaultValue("10")@QueryParam("limit") Integer limit,
                           @DefaultValue("0")@QueryParam("offset") Integer offset,
                           @DefaultValue("title")@QueryParam("orderby") String orderby,
                           @DefaultValue("ASC")@QueryParam("direction") String direction){
        HashMap<String,String> query = new HashMap<>();
        if(title != null)
            query.put("title", title);
        if(year != null)
            query.put("year", year.toString());
        if(director != null)
            query.put("director", director);
        if(genre != null)
            query.put("genre", genre);
        if(hidden != null)
            query.put("hidden", hidden.toString());
        if(limit != null)
            query.put("limit", limit.toString());
        if(offset != null)
            query.put("offset", offset.toString());
        if(orderby != null)
            query.put("orderby", orderby);
        if(direction != null)
            query.put("direction", direction);

        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getSearchPath(),query);
    }

    @Path("/browse/{phrase}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response browse(@Context HttpHeaders headers,
                                 @PathParam("phrase") String phrase,
                                 @DefaultValue("10")@QueryParam("limit") Integer limit,
                                 @DefaultValue("0")@QueryParam("offset") Integer offset,
                                 @DefaultValue("title")@QueryParam("orderby") String orderby,
                                 @DefaultValue("ASC")@QueryParam("direction") String direction){
        HashMap<String,String> query = new HashMap<>();
        if(limit != null)
            query.put("limit", limit.toString());
        if(offset != null)
            query.put("offset", offset.toString());
        if(orderby != null)
            query.put("orderby", orderby);
        if(direction != null)
            query.put("direction", direction);
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getBrowsePath()+phrase,query);
    }

    @Path("/get/{movie_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovie(@Context HttpHeaders headers,
                             @PathParam("movie_id") String movie_id){
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getGetPath(),null);
    }

    @Path("/thumbnail")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response thumbnail(@Context HttpHeaders headers, byte[] jsonBytes){
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Post(headers,jsonBytes,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getThumbnailPath());
    }

    @Path("/people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response people(@Context HttpHeaders headers,
                           @QueryParam("name")String name,
                           @DefaultValue("10")@QueryParam("limit") Integer limit,
                           @DefaultValue("0")@QueryParam("offset") Integer offset,
                           @DefaultValue("title")@QueryParam("orderby") String orderby,
                           @DefaultValue("ASC")@QueryParam("direction") String direction){
        HashMap<String,String> query = new HashMap<>();
        if(name != null)
            query.put("name", name);
        if(limit != null)
            query.put("limit", limit.toString());
        if(offset != null)
            query.put("offset", offset.toString());
        if(orderby != null)
            query.put("orderby", orderby);
        if(direction != null)
            query.put("direction", direction);

        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getPeoplePath(),query);
    }

    @Path("/people/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearch(@Context HttpHeaders headers,
                                 @QueryParam("name")String name,
                                 @QueryParam("birthday")String birthday,
                                 @QueryParam("movie_title")String movie_title,
                                 @DefaultValue("10")@QueryParam("limit") Integer limit,
                                 @DefaultValue("0")@QueryParam("offset") Integer offset,
                                 @DefaultValue("title")@QueryParam("orderby") String orderby,
                                 @DefaultValue("ASC")@QueryParam("direction") String direction){
        HashMap<String,String> query = new HashMap<>();
        if(name != null)
            query.put("name", name);
        if(birthday != null)
            query.put("birthday", birthday);
        if(movie_title != null)
            query.put("movie_title", movie_title);
        if(limit != null)
            query.put("limit", limit.toString());
        if(offset != null)
            query.put("offset", offset.toString());
        if(orderby != null)
            query.put("orderby", orderby);
        if(direction != null)
            query.put("direction", direction);

        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getPeopleSearchPath(),query);
    }

    @Path("/people/get/{person_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleGet(@Context HttpHeaders headers,@PathParam("person_id") String person_id){
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        return Utility.Get(headers,moviesConfigs.getScheme()+moviesConfigs.getHostName()+":"+moviesConfigs.getPort()+moviesConfigs.getPath(),moviesConfigs.getPeopleGetPath()+person_id,null);
    }
}
