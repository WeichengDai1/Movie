package edu.uci.ics.weiched.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.idm.IDMService;
import edu.uci.ics.weiched.service.idm.logger.ServiceLogger;
import edu.uci.ics.weiched.service.idm.models.RegisterRequestModel;
import edu.uci.ics.weiched.service.idm.models.RegisterResponseModel;
import org.apache.commons.codec.binary.Hex;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import static edu.uci.ics.weiched.service.idm.security.Crypto.genSalt;
import static edu.uci.ics.weiched.service.idm.security.Crypto.hashPassword;
import static java.lang.StrictMath.random;
import static java.lang.System.exit;

@Path("register")
public class Register {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response response(@Context HttpHeaders headers, String jsonText) {
        RegisterResponseModel responseModel;
        RegisterRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new RegisterResponseModel(resultCode, "JSON parse exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new RegisterResponseModel(resultCode, "JSON mapping exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new RegisterResponseModel(resultCode, "Internal server error.");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        String email = requestModel.getEmail();
        char[] password = requestModel.getPassword();

        if(email== null){
            String message = "Email address has invalid length.";
            responseModel = new RegisterResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if(password == null||password.length==0){
            responseModel = new RegisterResponseModel(-12, "Password has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (password.length > 16) {
            String message = "Password has invalid length. <=16";
            responseModel = new RegisterResponseModel(-12, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email.length() >= 50 || email.length() <= 6) {
            String message = "Email address has invalid length.";
            responseModel = new RegisterResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        String pattern = "[a-z0-9A-Z]+@([a-zA-Z0-9]+)\\.([a-zA-Z]+)";
        boolean ismatched = Pattern.matches(pattern, email);
        if (!ismatched) {
            String message = "Email address has invalid format.";
            responseModel = new RegisterResponseModel(-11, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (password.length < 7) {
            String message = "Password does not meet length requirements. >=7";
            responseModel = new RegisterResponseModel(12, message);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        int upper = 0;
        int lower = 0;
        int num = 0;
        for (int i = 0; i < password.length; i++) {
            if ((int) password[i] >= 48 && (int) password[i] <= 57) {
                num = 1;
            } else if ((int) password[i] >= 65 && (int) password[i] <= 90) {
                upper = 1;
            } else if ((int) password[i] >= 97 && (int) password[i] <= 122) {
                lower = 1;
            }
//            } else {
//                String message = "Password does not meet character requirements.";
//                responseModel = new RegisterResponseModel(13, message);
//                return Response.status(Response.Status.OK).entity(responseModel).build();
//            }
        }
        if (upper * lower * num == 0) {
            String message = "Password does not meet character requirements.";
            responseModel = new RegisterResponseModel(13, message);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        ArrayList<String> arr = list_users();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.contains(email)) {
                String message = "Email already in use.";
                responseModel = new RegisterResponseModel(16, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }

        byte[] gensalt = genSalt();
        byte[] hashed = hashPassword(password, gensalt, 10000, 512);
        try{
            String ins = "INSERT INTO user (email,status,plevel,salt,pword) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(ins);
            stmt.setString(1, email);
            stmt.setInt(2, 1);
            stmt.setInt(3, 5);
            stmt.setString(4, Hex.encodeHexString(gensalt));
            stmt.setString(5, Hex.encodeHexString(hashed));
            stmt.executeUpdate();
            String message = "User registered successfully.";
            responseModel = new RegisterResponseModel(110, message);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("list users failed");
        }
        String message = "Internal server error.";
        responseModel = new RegisterResponseModel(-1, message);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }

    private ArrayList<String> list_users() {
        ArrayList<String> users = new ArrayList<>();
        try {
            PreparedStatement stmt = IDMService.getCon().prepareStatement("SELECT email FROM user");
            ServiceLogger.LOGGER.info("getting all users");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String email = rs.getString("email");
                users.add(email);
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("list users failed");
        }
        return users;
    }
}