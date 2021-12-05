package edu.uci.ics.weiched.service.gateway.resources;

import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.configs.BillingConfigs;
import edu.uci.ics.weiched.service.gateway.util.Utility;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/billing")
public class BillingResources {
    @Path("/cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartInsert(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getCartInsertPath());
    }

    @Path("/cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartUpdate(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getCartUpdatePath());
    }

    @Path("/cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartDelete(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getCartDeletePath());
    }

    @Path("/cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartRetrieve(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getCartRetrievePath());
    }

    @Path("/cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cartClear(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getCartClearPath());
    }

    @Path("/order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderPlace(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getOrderPlacePath());
    }

    @Path("/order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderRetrieve(@Context HttpHeaders headers, byte[] jsonBytes){
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Post(headers, jsonBytes, config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getOrderRetrievePath());
    }

    @Path("/order/complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderComplete(@Context HttpHeaders headers, @QueryParam("token") String token, @QueryParam("payer_id") String payer_id){
        HashMap<String,String> query = new HashMap<>();
        query.put("token", token);
        query.put("payer_id", payer_id);
        BillingConfigs config = GatewayService.getBillingConfigs();
        return Utility.Get(headers,config.getScheme()+config.getHostName()+":"+config.getPort()+config.getPath(), config.getOrderCompletePath(), query);
    }
}
