package edu.uci.ics.weiched.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.billing.BillingService;
import edu.uci.ics.weiched.service.billing.logger.ServiceLogger;
import edu.uci.ics.weiched.service.billing.models.*;
import edu.uci.ics.weiched.service.billing.util.Utility;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/cart")
public class CartRetrieveResource {
    @Path("/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCart(@Context HttpHeaders headers, String jsonText){
        CartRetrieveRequestModel requestModel;
        CartRetrieveResponseModel responseModel;

        try{
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText,CartRetrieveRequestModel.class);
        }catch (JsonParseException e){
            responseModel = new CartRetrieveResponseModel(-3,"JSON Parse Exception.");
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }catch (JsonMappingException e) {
            responseModel = new CartRetrieveResponseModel(-2,"JSON Mapping Exception.");
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } catch (Exception e) {
            responseModel = new CartRetrieveResponseModel(-1,"Internal Server Error.");
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        String query =  "SELECT JSON_ARRAYAGG(JSON_OBJECT('email', t.email, 'movie_id', t.movie_id, 'quantity', t.quantity, 'unit_price', t.unit_price, 'discount', t.discount)) as Items\n" +
                "FROM (SELECT c.email, c.movie_id, c.quantity, mp.unit_price, mp.discount\n"+
                "FROM cart as c\n"+
                "INNER JOIN movie_price as mp on c.movie_id = mp.movie_id\n"+
                "WHERE c.email LIKE ?) as t";

        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getEmail());
            ResultSet rs = ps.executeQuery();
            ItemPartialModel[] models;
            if(rs.next()){
                models = Utility.mapping(rs.getString("Items"),ItemPartialModel[].class);
                if(models==null||models.length==0){
                    responseModel = new CartRetrieveResponseModel(312,"Shopping cart item does not exist.");
                    Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                    builder.header("email",headers.getHeaderString("email"));
                    builder.header("session_id",headers.getHeaderString("session_id"));
                    builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                    return builder.build();
                }else{
                    ArrayList<String> movie_ids = new ArrayList<>();
                    for(ItemPartialModel itemModel:models){
                        movie_ids.add(itemModel.getMovie_id());
                    }
                    HashMap<String, ThumbnailModel> thumbnailModelHashMap = Utility.getThumbnail(movie_ids);
                    ArrayList<ItemModel> itemModels = new ArrayList<>();
                    for(ItemPartialModel itemModel:models){
                        ThumbnailModel model = thumbnailModelHashMap.get(itemModel.getMovie_id());
                        itemModels.add(new ItemModel(itemModel.getEmail(),itemModel.getUnit_price(),
                                itemModel.getDiscount(),itemModel.getQuantity(),itemModel.getMovie_id(),model.getTitle(),
                                model.getBackdrop_path(),model.getPoster_path()));
                    }
                    ItemModel[] temp = itemModels.toArray(new ItemModel[itemModels.size()]);
                    responseModel=new CartRetrieveResponseModel(3130,"Shopping cart retrieved successfully.",temp);
                    Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                    builder.header("email",headers.getHeaderString("email"));
                    builder.header("session_id",headers.getHeaderString("session_id"));
                    builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                    return builder.build();
                }
            }else {
                responseModel = new CartRetrieveResponseModel(312,"Shopping cart item does not exist.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseModel = new CartRetrieveResponseModel(3150,"Shopping cart operation failed.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }
    }
}
