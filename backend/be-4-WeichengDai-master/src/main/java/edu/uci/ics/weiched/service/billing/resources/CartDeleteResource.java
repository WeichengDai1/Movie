package edu.uci.ics.weiched.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.billing.BillingService;
import edu.uci.ics.weiched.service.billing.logger.ServiceLogger;
import edu.uci.ics.weiched.service.billing.models.CartDeleteModel;
import edu.uci.ics.weiched.service.billing.models.CartDeleteRequestModel;
import edu.uci.ics.weiched.service.billing.models.CartInsertResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/cart")
public class CartDeleteResource {
    @Path("/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCart(@Context HttpHeaders headers, String jsonText){
        CartDeleteModel cartDeleteModel;
        CartDeleteRequestModel requestModel;
        CartInsertResponseModel responseModel;
        try{
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText,CartDeleteRequestModel.class);
        }catch(JsonParseException e){
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

        String email = requestModel.getEmail();
        String movie_id = requestModel.getMovie_id();

        String query = "DELETE FROM cart WHERE email = ? AND movie_id = ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ps.setString(2,movie_id);
            int rsLength = ps.executeUpdate();
            if(rsLength<1){
                responseModel = new CartInsertResponseModel(312,"Shopping cart item does not exist.");
                ServiceLogger.LOGGER.info("Shopping cart item does not exist.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }else{
                responseModel = new CartInsertResponseModel(3120,"Shopping cart item deleted successfully.");
                ServiceLogger.LOGGER.info("Shopping cart item deleted successfully.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseModel = new CartInsertResponseModel(3150,"Shopping cart operation failed.");
            ServiceLogger.LOGGER.info("Shopping cart operation failed.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }catch (Exception e){
            responseModel = new CartInsertResponseModel(-1,"Internal Server Error.");
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
    }
}
