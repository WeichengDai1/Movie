package edu.uci.ics.weiched.service.gateway.resources;

import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.configs.IdmConfigs;
import edu.uci.ics.weiched.service.gateway.logger.ServiceLogger;
import edu.uci.ics.weiched.service.gateway.util.Utility;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/idm")
public class IdmResources {
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@Context HttpHeaders headers, byte[] jsonBytes){
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        ServiceLogger.LOGGER.info("redirecting to idm/register");
        return Utility.Post(headers, jsonBytes, idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+idmConfigs.getPath(), idmConfigs.getRegisterPath());
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers, byte[] jsonBytes){
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        ServiceLogger.LOGGER.info("redirecting to idm/login");
        return Utility.Post(headers, jsonBytes, idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+idmConfigs.getPath(), idmConfigs.getLoginPath());
    }

    @Path("/session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response session(@Context HttpHeaders headers, byte[] jsonBytes){
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        ServiceLogger.LOGGER.info("redirecting to idm/session");
        return Utility.Post(headers, jsonBytes, idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+idmConfigs.getPath(), idmConfigs.getSessionPath());
    }

    @Path("/privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response privilege(@Context HttpHeaders headers, byte[] jsonBytes){
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        ServiceLogger.LOGGER.info("redirecting to idm/privilege");
        return Utility.Post(headers, jsonBytes, idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+idmConfigs.getPath(), idmConfigs.getPrivilegePath());
    }
}
