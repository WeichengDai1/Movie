package edu.uci.ics.weiched.service.billing.util;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.orders.*;
import edu.uci.ics.weiched.service.billing.BillingService;
import edu.uci.ics.weiched.service.billing.configs.IdmConfigs;
import edu.uci.ics.weiched.service.billing.configs.MoviesConfigs;
import edu.uci.ics.weiched.service.billing.logger.ServiceLogger;
import edu.uci.ics.weiched.service.billing.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utility {
    public static PreparedStatement preparedStatement(String query, ArrayList<Object> searchArray) throws SQLException {
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        int index = 1;
        for(Object p: searchArray){
            ps.setObject(index++, p);
        }
        ServiceLogger.LOGGER.info(ps.toString());
        return ps;
    }

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

        IdmConfigs temp = BillingService.getIdmConfigs();
        WebTarget webTarget = client.target(temp.getScheme()+temp.getHostName()+":"+temp.getPort()+temp.getPath()).path(BillingService.getIdmConfigs().getPrivilegePath());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        PrivilegeResponseModel responseModel = Utility.mapping(response.readEntity(String.class), PrivilegeResponseModel.class);
        if(responseModel == null)
            return false;
        ServiceLogger.LOGGER.info("get the responseModel "+responseModel.getResultCode());
        return responseModel.getResultCode() == 140;
    }

    public static HashMap<String, ThumbnailModel> getThumbnail(ArrayList<String> movie_ids)
    {
        String[] temparr = movie_ids.toArray(new String[0]);
        ThumbnailRequestModel requestModel = new ThumbnailRequestModel(temparr);

        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);


        ServiceLogger.LOGGER.info("Building WebTarget...");
        MoviesConfigs temp = BillingService.getMoviesConfigs();
        WebTarget webTarget = client.target(temp.getScheme()+temp.getHostName()+":"+temp.getPort()+temp.getPath()).path(temp.getThumbnailPath());
        ServiceLogger.LOGGER.info("Sending to path: " + temp.getPath() + temp.getThumbnailPath());

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);


        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");
        ThumbnailResponseModel responseModel = Utility.mapping(response.readEntity(String.class), ThumbnailResponseModel.class);
        ThumbnailModel[] movieInfo = new ThumbnailModel[0];
        if (responseModel != null) {
            movieInfo = responseModel.getThumbnails();
        }else{
            ServiceLogger.LOGGER.warning("movieInfo is empty");
        }
        HashMap<String, ThumbnailModel> thumbnailModelHashMap = new HashMap<>();
        for(ThumbnailModel info : movieInfo)
            thumbnailModelHashMap.put(info.getMovie_id(), info);
        return thumbnailModelHashMap;
    }



}
