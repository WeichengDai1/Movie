package edu.uci.ics.weiched.service.basic.resources;

import edu.uci.ics.weiched.service.basic.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/hello")
public class TestPage {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(){
        System.err.println("Hello!");
        ServiceLogger.LOGGER.info("Hello!");
        return Response.status(Status.OK).build();
    }

}
