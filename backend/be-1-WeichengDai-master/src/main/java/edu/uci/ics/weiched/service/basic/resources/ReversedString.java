package edu.uci.ics.weiched.service.basic.resources;

import edu.uci.ics.weiched.service.basic.logger.ServiceLogger;
import edu.uci.ics.weiched.service.basic.models.StringRequestModel;
import edu.uci.ics.weiched.service.basic.models.StringResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/reverse")
public class ReversedString {
    @Path("/{string}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response reversed(@Context HttpHeaders headers, @PathParam("string") String raw){
        StringRequestModel requestModel = new StringRequestModel(raw);
        StringResponseModel responseModel;

        ServiceLogger.LOGGER.info("Received get request");
        ServiceLogger.LOGGER.info("Request: " + raw);

        if (requestModel.getRaw().isEmpty()){
            responseModel = new StringResponseModel(11, "String is empty.", null);
            return Response.status(Status.OK).entity(responseModel).build();
        }

        char[] str = requestModel.getRaw().toCharArray();
        for (int i = 0;i<str.length;i++){
            if (((int)str[i]<=47)&&((int)str[i]!=32)||((int)str[i]>=123)){
                responseModel = new StringResponseModel(12, "String contains invalid characters.", null);
                return Response.status(Status.OK).entity(responseModel).build();
            }else if(((int)str[i]>=58)&&((int)str[i]<=64)){
                responseModel = new StringResponseModel(12, "String contains invalid characters.", null);
                return Response.status(Status.OK).entity(responseModel).build();
            } else if(((int)str[i]<=94)&&(int)str[i]>=91){
                responseModel = new StringResponseModel(12, "String contains invalid characters.", null);
                return Response.status(Status.OK).entity(responseModel).build();
            }else if((int)str[i]==96){
                responseModel = new StringResponseModel(12, "String contains invalid characters.", null);
                return Response.status(Status.OK).entity(responseModel).build();
            }
        }


        byte[] StrAsByteArray = requestModel.getRaw().getBytes();
        byte[] result = new byte[StrAsByteArray.length];

        for (int i = 0; i<StrAsByteArray.length; i++){
            result[i] = StrAsByteArray[StrAsByteArray.length-i-1];
        }

        String outcome = new String(result);
        ServiceLogger.LOGGER.info("Reversed successful!");
        responseModel = new StringResponseModel(10, "String successfully reversed.", outcome);
        return Response.status(Status.OK).entity(responseModel).build();
    }
}
