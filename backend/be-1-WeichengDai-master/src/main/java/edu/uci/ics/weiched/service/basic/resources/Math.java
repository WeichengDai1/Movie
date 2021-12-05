package edu.uci.ics.weiched.service.basic.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.weiched.service.basic.logger.ServiceLogger;
import edu.uci.ics.weiched.service.basic.models.MathRequestModel;
import edu.uci.ics.weiched.service.basic.models.MathResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("math")
public class Math {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response response(@Context HttpHeaders headers, String jsonText){
        MathResponseModel responseModel;
        MathRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, MathRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new MathResponseModel(resultCode, "JSON parse exception.", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new MathResponseModel(resultCode, "JSON mapping exception.", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new MathResponseModel(resultCode, "Internal server error.", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        int x = requestModel.getX();
        int y = requestModel.getY();
        int z = requestModel.getZ();

        int result = x*y+z;
        if ((x<100)&&(x>0)&&(y<100)&&(y>0)&&(z<=10)&&(z>=-10)){
            responseModel = new MathResponseModel(20, "Calculation successful.", result);
        }else{
            responseModel = new MathResponseModel(21, "Data contains invalid integers.", null);
        }
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
