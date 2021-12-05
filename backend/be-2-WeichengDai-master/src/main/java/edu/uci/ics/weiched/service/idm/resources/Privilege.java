package edu.uci.ics.weiched.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.idm.IDMService;
import edu.uci.ics.weiched.service.idm.logger.ServiceLogger;
import edu.uci.ics.weiched.service.idm.models.PrivilegeRequestModel;
import edu.uci.ics.weiched.service.idm.models.PrivilegeResponseModel;
import edu.uci.ics.weiched.service.idm.models.RegisterRequestModel;
import edu.uci.ics.weiched.service.idm.models.RegisterResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.System.exit;


@Path("privilege")
public class Privilege {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response response(@Context HttpHeaders headers, String jsonText) {
        PrivilegeResponseModel responseModel;
        PrivilegeRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new PrivilegeResponseModel(resultCode, "JSON parse exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new PrivilegeResponseModel(resultCode, "JSON mapping exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new PrivilegeResponseModel(resultCode, "Internal server error.");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        String email = requestModel.getEmail();
        int plevel = requestModel.getPlevel();

        if(email== null){
            String message = "Email address has invalid length.";
            responseModel = new PrivilegeResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if(plevel>=6 || plevel< 1){
            String message = "Privilege level out of valid range.";
            responseModel = new PrivilegeResponseModel(-14, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email.length() >= 50 || email.length() <= 6) {
            String message = "Email address has invalid length.";
            responseModel = new PrivilegeResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (!email.matches("[a-z0-9A-Z]+@([a-zA-Z0-9]+)\\.([a-zA-Z]+)")){
            responseModel = new PrivilegeResponseModel(-11, "Email address has invalid format.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        try{
            String query = "SELECT plevel FROM user WHERE email = ?";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(query);
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                String message = "User not found.";
                responseModel = new PrivilegeResponseModel(14, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            int retrievedPlevel = rs.getInt("plevel");
            if (retrievedPlevel<= plevel){
                String message = "User has sufficient privilege level.";
                responseModel = new PrivilegeResponseModel(140, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }else {
                String message = "User has insufficient privilege level.";
                responseModel = new PrivilegeResponseModel(141, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("list users failed");
        }
        responseModel = new PrivilegeResponseModel(-1, "Internal server error.");
        ServiceLogger.LOGGER.severe("Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }

}
