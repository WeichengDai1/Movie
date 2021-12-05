package edu.uci.ics.weiched.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.idm.IDMService;
import edu.uci.ics.weiched.service.idm.logger.ServiceLogger;
import edu.uci.ics.weiched.service.idm.models.*;
import edu.uci.ics.weiched.service.idm.security.Session;
import edu.uci.ics.weiched.service.idm.security.Token;

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
import java.util.regex.Pattern;

@Path("session")
public class SessionResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response response(@Context HttpHeaders headers, String jsonText){
        SessionResponseModel responseModel ;
        SessionRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, SessionRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new SessionResponseModel(resultCode, "JSON parse exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new SessionResponseModel(resultCode, "JSON mapping exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new SessionResponseModel(resultCode, "Internal server error.");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        String email = requestModel.getEmail();
        String session = requestModel.getSession_id();

        if(email == null){
            responseModel = new SessionResponseModel(-10, "Email address has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

//        Token token = Token.rebuildToken(session);
//        boolean isval = token.validate(session);
        if(session.length()!=128){
            String message = "Token has invalid length.";
            responseModel = new SessionResponseModel(-13, message);
            ServiceLogger.LOGGER.warning("token length invalid.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email.length() >= 50 || email.length() <= 6) {
            String message = "Email address has invalid length.";
            responseModel = new SessionResponseModel(-10, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        String pattern = "[a-z0-9A-Z]+@([a-zA-Z0-9]+)\\.([a-zA-Z]+)";
        boolean ismatched = Pattern.matches(pattern, email);
        if (!ismatched) {
            String message = "Email address has invalid format.";
            responseModel = new SessionResponseModel(-11, message);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        try{
            String query = "SELECT email FROM user WHERE email = ?";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(query);
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                String message = "User not found.";
                responseModel = new SessionResponseModel(14, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("looking into users failed.");
        }

        try{
            String query = "SELECT * FROM session WHERE session_id = ?";
            PreparedStatement stmt = IDMService.getCon().prepareStatement(query);
            stmt.setString(1,session);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                String message = "Session not found.";
                responseModel = new SessionResponseModel(134, message);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }else{
                int status = rs.getInt("status");
                if(status == 1){
                    Session currentSession = Session.rebuildSession(email,Token.rebuildToken(session),rs.getTimestamp("time_created"),
                            rs.getTimestamp("last_used"), rs.getTimestamp("expr_time"));
                    if(!currentSession.isDataTimedOut()){
                        currentSession.update();
                        String updateQuery = "UPDATE session SET last_used = ? WHERE session_id = ?";
                        PreparedStatement updt = IDMService.getCon().prepareStatement(updateQuery);
                        updt.setTimestamp(1,currentSession.getLastUsed());
                        updt.setString(2, session);
                        int istrue = updt.executeUpdate();
                        if(istrue == 0){
                            ServiceLogger.LOGGER.warning("update last_used time failed.");
                        }
                        String message = "Session is active.";
                        responseModel = new SessionResponseModel(130, message);
                        responseModel.setSession_id(session);
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }else if(currentSession.NoNeedToReLogin()){
                        String updateQuery = "UPDATE session SET status = 4 WHERE session_id = ?";
                        PreparedStatement updtqry = IDMService.getCon().prepareStatement(updateQuery);
                        updtqry.setString(1,session);
                        int issuccess = updtqry.executeUpdate();
                        if(issuccess == 0){
                            ServiceLogger.LOGGER.warning("update close to expired session to revoked failed.");
                        }
                        Session newsession = new Session(email);
                        String ins = "INSERT INTO session (session_id,email,status,time_created,last_used,expr_time) VALUES (?,?,?,?,?,?)";
                        PreparedStatement insertstmt = IDMService.getCon().prepareStatement(ins);
                        insertstmt.setString(1,newsession.getSessionID().toString());
                        insertstmt.setString(2,newsession.getEmail());
                        insertstmt.setInt(3,1);
                        insertstmt.setTimestamp(4,newsession.getTimeCreated());
                        insertstmt.setTimestamp(5,newsession.getLastUsed());
                        insertstmt.setTimestamp(6, newsession.getExprTime());
                        int issuccessful = insertstmt.executeUpdate();
                        if(issuccessful!=1){
                            ServiceLogger.LOGGER.warning("insert new session failed.");
                        }
                        String message = "Session is active.";
                        responseModel = new SessionResponseModel(130, message);
                        responseModel.setSession_id(newsession.getSessionID().toString());
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    } else if(!currentSession.isDataExpired()){
                        String updateQuery = "UPDATE session SET status = 4 WHERE session_id = ?";
                        PreparedStatement updtqry = IDMService.getCon().prepareStatement(updateQuery);
                        updtqry.setString(1,session);
                        int issuccess = updtqry.executeUpdate();
                        if(issuccess == 0){
                            ServiceLogger.LOGGER.warning("update session to revoked failed.");
                        }
                        String message = "Session is revoked.";
                        responseModel = new SessionResponseModel(133, message);
                        responseModel.setSession_id(currentSession.getSessionID().toString());
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }else {
                        String updateQuery = "UPDATE session SET status = 3 WHERE session_id = ?";
                        PreparedStatement updtqry = IDMService.getCon().prepareStatement(updateQuery);
                        updtqry.setString(1,session);
                        int issuccess = updtqry.executeUpdate();
                        if(issuccess == 0){
                            ServiceLogger.LOGGER.warning("update session to expired failed.");
                        }
                        String message = "Session is expired.";
                        responseModel = new SessionResponseModel(131, message);
                        responseModel.setSession_id(currentSession.getSessionID().toString());
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }

                }else if(status == 2){
                    String message = "Session is closed.";
                    responseModel = new SessionResponseModel(132, message);
//                    responseModel.setSession_id(session);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }else if(status == 3){
                    String message = "Session is expired.";
                    responseModel = new SessionResponseModel(131, message);
//                    responseModel.setSession_id(session);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }else if(status == 4){
                    String message = "Session is revoked.";
                    responseModel = new SessionResponseModel(133, message);
//                    responseModel.setSession_id(session);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("looking into sessions failed.");
        }
        responseModel = new SessionResponseModel(-1, "Internal server error.");
        ServiceLogger.LOGGER.severe("Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }
}
