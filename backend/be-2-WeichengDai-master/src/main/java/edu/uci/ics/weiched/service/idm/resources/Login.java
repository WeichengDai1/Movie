package edu.uci.ics.weiched.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.idm.IDMService;
import edu.uci.ics.weiched.service.idm.logger.ServiceLogger;
import edu.uci.ics.weiched.service.idm.models.*;
import edu.uci.ics.weiched.service.idm.security.Session;
import edu.uci.ics.weiched.service.idm.security.Token;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.regex.Pattern;

import static edu.uci.ics.weiched.service.idm.security.Crypto.hashPassword;

@Path("login")
public class Login {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response response(@Context HttpHeaders headers, String jsonText){
        LoginRequestModel requestModel;
        LoginResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, LoginRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new LoginResponseModel(resultCode, "JSON parse exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new LoginResponseModel(resultCode, "JSON mapping exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new LoginResponseModel(resultCode, "Internal server error.");
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
            responseModel = new LoginResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if(password == null){
            responseModel = new LoginResponseModel(-12, "Password has invalid length.");
            ServiceLogger.LOGGER.info("Password has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if(password.length> 16 || password.length<7){
            responseModel = new LoginResponseModel(-12, "Password has invalid length.");
            ServiceLogger.LOGGER.info("Password has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email.length() >= 50 || email.length() <= 6) {
            String message = "Email address has invalid length.";
            responseModel = new LoginResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        String pattern = "[a-z0-9A-Z]+@([a-zA-Z0-9]+)\\.([a-zA-Z]+)";
        boolean ismatched = Pattern.matches(pattern, email);
        if (!ismatched) {
            String message = "Email address has invalid format.";
            responseModel = new LoginResponseModel(-11, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        try{
            String query = "SELECT email FROM user WHERE email = ?";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(query);
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                String message = "User not found.";
                responseModel = new LoginResponseModel(14, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("looking into users failed.");
        }

        try{
            String query = "SELECT salt,pword FROM user WHERE email = ?";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(query);
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String salt = rs.getString("salt");
                String pword = rs.getString("pword");
                byte[] bytesalt = Hex.decodeHex(salt);
                byte[] bytepword = Hex.decodeHex(pword);
                byte[] computedpword = hashPassword(password, bytesalt, 10000, 512);
                if(!Arrays.equals(bytepword,computedpword)){
                    String message = "Passwords do not match.";
                    responseModel = new LoginResponseModel(11, message);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }else{
                    Session session = Session.createSession(email);
                    try{
                        String sessionquery = "SELECT * FROM session WHERE email = ? AND status = 1";
                        PreparedStatement sessionstmt = IDMService.getCon().prepareStatement(sessionquery);
                        sessionstmt.setString(1,email);
                        int exist = sessionstmt.executeUpdate();
                        if(exist == 1){
                            String sessionupdate = "UPDATE session SET status = 4 WHERE email = ?";
                            PreparedStatement updatestmt = IDMService.getCon().prepareStatement(sessionupdate);
                            updatestmt.setString(1,email);
                            boolean success = updatestmt.execute();
                            if(!success){
                                ServiceLogger.LOGGER.warning("update login session failed.");
                            }
                        }
                    }catch (SQLException e){
                        ServiceLogger.LOGGER.warning("user have never logged in.");
                    }
                    String new_email = session.getEmail();
                    Token new_id = session.getSessionID();
                    Timestamp timeCreated = session.getTimeCreated();
                    Timestamp exprTime = session.getExprTime();
                    Timestamp lastUsed = session.getLastUsed();
                    try{
                        String ins = "INSERT INTO session (session_id,email,status,time_created,last_used,expr_time) VALUES (?,?,?,?,?,?)";
                        PreparedStatement insertstmt = IDMService.getCon().prepareStatement(ins);
                        insertstmt.setString(1,new_id.toString());
                        insertstmt.setString(2,new_email);
                        insertstmt.setInt(3,1);
                        insertstmt.setTimestamp(4,timeCreated);
                        insertstmt.setTimestamp(5,lastUsed);
                        insertstmt.setTimestamp(6, exprTime);
                        int issuccess = insertstmt.executeUpdate();
                        if(issuccess!=1){
                            ServiceLogger.LOGGER.warning("insert new session failed.");
                        }else{
                            String message = "User logged in successfully.";
                            responseModel = new LoginResponseModel(120, message);
                            responseModel.setSession_id(new_id.toString());
                            return Response.status(Response.Status.OK).entity(responseModel).build();
                        }
                    }catch (SQLException e){
                        ServiceLogger.LOGGER.warning("insert new session failed.");
                    }
                }
            }

        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("looking into users failed.");
        } catch (DecoderException e) {
            ServiceLogger.LOGGER.warning("decoding failed");
        }
        responseModel = new LoginResponseModel(-1, "Internal server error.");
        ServiceLogger.LOGGER.severe("Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }
}
