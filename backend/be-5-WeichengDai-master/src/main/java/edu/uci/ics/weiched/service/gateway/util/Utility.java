package edu.uci.ics.weiched.service.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.configs.IdmConfigs;
import edu.uci.ics.weiched.service.gateway.logger.ServiceLogger;
import edu.uci.ics.weiched.service.gateway.models.*;
import edu.uci.ics.weiched.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.weiched.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.weiched.service.gateway.transaction.TransactionGenerator;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

public class Utility {
    public static <T> T mapping(String jsonText, Class<T> className)
    {
        if(jsonText == null) {
            ServiceLogger.LOGGER.info("Nothing to map.");
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();

        ServiceLogger.LOGGER.info("Mapping object: " + className.getName());

        try {
            return mapper.readValue(jsonText, className);
        } catch (IOException e) {
            ServiceLogger.LOGGER.info("Mapping Object Failed: " + e.getMessage());
            return null;
        }
    }

    public static boolean getPlevel(String email, int plevel)
    {
        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email, plevel);
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        IdmConfigs temp = GatewayService.getIdmConfigs();
        WebTarget webTarget = client.target(temp.getScheme()+temp.getHostName()+":"+temp.getPort()+temp.getPath()).path(GatewayService.getIdmConfigs().getPrivilegePath());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        PrivilegeResponseModel responseModel = Utility.mapping(response.readEntity(String.class), PrivilegeResponseModel.class);
        if(responseModel == null)
            return false;
        ServiceLogger.LOGGER.info("get the responseModel "+responseModel.getResultCode());
        return responseModel.getResultCode() == 140;
    }

    public static Response Post(HttpHeaders headers, byte[] jsonBytes, String url, String path){
        HeaderModel headerModel = new HeaderModel(headers);
        SessionResponseModel responseModel = isSessionActive(headerModel);
        if(headers.getHeaderString("email")!=null){
            if(responseModel!=null){
                ServiceLogger.LOGGER.warning("this session is not active");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder = builder.header("email", headerModel.getEmail());
                builder = builder.header("session_id", headerModel.getSession_id());
                builder = builder.header("transaction_id", headerModel.getTransaction_id());
                return builder.build();
            }
        }
            String transaction_id = TransactionGenerator.generate();
            ClientRequest clientRequest = new ClientRequest(headerModel.getEmail(),
                    headerModel.getSession_id(), transaction_id,
                    url, path, HTTPMethod.POST, jsonBytes, null);
            GatewayService.getThreadPool().putRequest(clientRequest);
            Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);

            if(headerModel.getEmail()!=null)
                builder = builder.header("email", headerModel.getEmail());
            if(headerModel.getSession_id()!= null)
                builder = builder.header("session_id", headerModel.getSession_id());
            if(headerModel.getTransaction_id()!= null)
                builder = builder.header("transaction_id", headerModel.getTransaction_id());
            builder = builder.header("transaction_id", transaction_id);
            builder = builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
            return builder.build();

    }

    public static Response Get(HttpHeaders headers, String url, String path, HashMap<String, String> query){
        HeaderModel headerModel = new HeaderModel(headers);
        SessionResponseModel responseModel = isSessionActive(headerModel);
        if(headerModel.getEmail()!=null) {
            if (responseModel != null) {
                ServiceLogger.LOGGER.warning("this session is not active");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder = builder.header("email", headers.getHeaderString("email"));
                builder = builder.header("session_id", headers.getHeaderString("session_id"));
                builder = builder.header("transaction_id", headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        }
            String transaction_id = TransactionGenerator.generate();
            ClientRequest clientRequest = new ClientRequest(headerModel.getEmail(),
                    headerModel.getSession_id(), transaction_id,
                    url, path, HTTPMethod.GET, null, query);
            GatewayService.getThreadPool().putRequest(clientRequest);
            Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
            if(headerModel.getEmail()!=null)
                builder = builder.header("email", headerModel.getEmail());
            if(headerModel.getSession_id()!= null)
                builder = builder.header("session_id", headerModel.getSession_id());
            if(headerModel.getTransaction_id()!= null)
                builder = builder.header("transaction_id", headerModel.getTransaction_id());
            builder = builder.header("transaction_id", transaction_id);
            builder = builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
            return builder.build();

    }

    public static SessionResponseModel isSessionActive(HeaderModel headerModel) {
        SessionRequestModel requestModel = new SessionRequestModel(headerModel.getEmail(), headerModel.getSession_id());
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        IdmConfigs idm = GatewayService.getIdmConfigs();
        WebTarget webTarget = client.target(idm.getScheme()+idm.getHostName()+":"+idm.getPort()+idm.getPath()).path(idm.getSessionPath());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        SessionResponseModel responseModel = Utility.mapping(response.readEntity(String.class), SessionResponseModel.class);
        if (responseModel != null && responseModel.getResultCode() == 130) {
            headerModel.setSession_id(responseModel.getSession_id());
            return null;
        }else if(responseModel == null){
            ServiceLogger.LOGGER.warning("idm session response is empty");
        }
        return responseModel;
    }
}
