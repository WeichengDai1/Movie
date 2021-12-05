package edu.uci.ics.weiched.service.gateway.resources;

import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.logger.ServiceLogger;
import edu.uci.ics.weiched.service.gateway.models.HeaderModel;
import edu.uci.ics.weiched.service.gateway.models.SessionResponseModel;
import edu.uci.ics.weiched.service.gateway.util.Utility;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/")
public class GatewayResource {
    @Path("report")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers){
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        HeaderModel headerModel = new HeaderModel(headers);

        if(email!=null){
            SessionResponseModel model = Utility.isSessionActive(headerModel);
            if(model!=null){
                ServiceLogger.LOGGER.warning("this session is not active");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(model);
                builder = builder.header("email", headers.getHeaderString("email"));
                builder = builder.header("session_id", headers.getHeaderString("session_id"));
                builder = builder.header("transaction_id", headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        }

        Connection connection;
        Response response;

        try{
            connection = GatewayService.getConnectionPoolManager().requestCon();
            String query = "SELECT * FROM responses WHERE transaction_id LIKE ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,transaction_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Response.ResponseBuilder builder = Response.status(rs.getInt("http_status")).entity(rs.getString("response"));
                builder = builder.header("email", email);
                builder = builder.header("session_id", session_id);
                builder = builder.header("transaction_id", transaction_id);
                response = builder.build();
                ps = connection.prepareStatement("DELETE FROM responses WHERE transaction_id LIKE ?");
                ps.setString(1,transaction_id);
                ps.execute();
                GatewayService.getConnectionPoolManager().releaseCon(connection);
                return response;
            }else{
                Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
                builder = builder.header("email",email);
                builder = builder.header("session_id", session_id);
                builder = builder.header("transaction_id", transaction_id);
                builder = builder.header("message", "Not ready.");
                builder = builder.header("request_delay",GatewayService.getThreadConfigs().getRequestDelay());
                response = builder.build();
                GatewayService.getConnectionPoolManager().releaseCon(connection);
                return response;
            }
        }catch (Exception e){
            e.printStackTrace();
            Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            builder = builder.header("email",email);
            builder = builder.header("session_id", session_id);
            builder = builder.header("transaction_id", transaction_id);
            response = builder.build();
            return response;
        }
    }
}
