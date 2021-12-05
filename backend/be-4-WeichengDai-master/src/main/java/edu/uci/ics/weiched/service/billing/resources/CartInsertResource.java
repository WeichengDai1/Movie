package edu.uci.ics.weiched.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.billing.BillingService;
import edu.uci.ics.weiched.service.billing.logger.ServiceLogger;
import edu.uci.ics.weiched.service.billing.models.CartInsertRequestModel;
import edu.uci.ics.weiched.service.billing.models.CartInsertResponseModel;
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

@Path("/cart")
public class CartInsertResource {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartInsert(@Context HttpHeaders headers, String jsonText){
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        CartInsertRequestModel requestModel;
        CartInsertResponseModel responseModel;
        try{
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText,CartInsertRequestModel.class);
        }catch (JsonParseException e){
            responseModel = new CartInsertResponseModel(-3,"JSON Parse Exception.");
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }catch (JsonMappingException e) {
            responseModel = new CartInsertResponseModel(-2,"JSON Mapping Exception.");
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } catch (Exception e) {
            responseModel = new CartInsertResponseModel(-1,"Internal Server Error.");
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        int quantity = requestModel.getQuantity();
        String movie_id = requestModel.getMovie_id();

        if(quantity<=0){
            responseModel = new CartInsertResponseModel(33,"Quantity has invalid value.");
            ServiceLogger.LOGGER.info("Quantity has invalid value.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }

        if(!Utility.getPlevel(requestModel.getEmail(),5)){
            responseModel = new CartInsertResponseModel(14,"User not found.");
            ServiceLogger.LOGGER.info("User not found.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }

        String query = "SELECT movie_id FROM movie_price WHERE movie_id LIKE ?";
        PreparedStatement ps;
        try {
            ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                responseModel = new CartInsertResponseModel(3150,"Shopping cart operation failed.");
                ServiceLogger.LOGGER.info("Shopping cart operation failed.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseModel = new CartInsertResponseModel(-1,"Internal Server Error.");
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        String insertQuery = "INSERT INTO cart(email, movie_id, quantity) VALUES (?, ?, ?)";
        PreparedStatement insertPs = null;
        try {
            insertPs = BillingService.getCon().prepareStatement(insertQuery);
            insertPs.setString(1,requestModel.getEmail());
            insertPs.setString(2,movie_id);
            insertPs.setInt(3,quantity);
            insertPs.executeUpdate();
        } catch (SQLException e) {
            responseModel = new CartInsertResponseModel(311,"Duplicate insertion.");
            ServiceLogger.LOGGER.info("Duplicate insertion.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }catch (Exception e) {
            responseModel = new CartInsertResponseModel(-1,"Internal Server Error.");
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        ServiceLogger.LOGGER.info("Insert success");
        responseModel = new CartInsertResponseModel(3100,"Shopping cart item inserted successfully.");
        Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id",headers.getHeaderString("transaction_id"));
        return builder.build();
    }
}
