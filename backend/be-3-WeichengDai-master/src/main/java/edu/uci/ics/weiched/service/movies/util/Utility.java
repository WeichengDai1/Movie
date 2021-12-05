package edu.uci.ics.weiched.service.movies.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.movies.MoviesService;
import edu.uci.ics.weiched.service.movies.configs.IdmConfigs;
import edu.uci.ics.weiched.service.movies.logger.ServiceLogger;
import edu.uci.ics.weiched.service.movies.models.PrivilegeRequestModel;
import edu.uci.ics.weiched.service.movies.models.PrivilegeResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.client.*;

public class Utility {
    public static PreparedStatement preparedStatement(String query, ArrayList<Object> searchArray) throws SQLException {
        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
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

        IdmConfigs temp = MoviesService.getIdmConfigs();
        WebTarget webTarget = client.target(temp.getScheme()+temp.getHostName()+":"+temp.getPort()+temp.getPath()).path(MoviesService.getIdmConfigs().getPrivilegePath());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        PrivilegeResponseModel responseModel = Utility.mapping(response.readEntity(String.class), PrivilegeResponseModel.class);
        if(responseModel == null)
            return false;
        return responseModel.getResultCode() == 140;
    }
}
