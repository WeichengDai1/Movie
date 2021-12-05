package edu.uci.ics.weiched.service.billing.resources;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import edu.uci.ics.weiched.service.billing.BillingService;
import edu.uci.ics.weiched.service.billing.configs.MoviesConfigs;
import edu.uci.ics.weiched.service.billing.configs.ServiceConfigs;
import edu.uci.ics.weiched.service.billing.logger.ServiceLogger;
import edu.uci.ics.weiched.service.billing.models.*;
import edu.uci.ics.weiched.service.billing.util.Utility;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/order")
public class OrderResource {

    private String clientId = "AaHsqcwMh4Or9-Q5RpbEVIA0tJi30QctX2fZS10VkRLy0J8Oh1R9l-PWhyB1hIVQ_xHpOo-KM96ZkuGQ";
    private String clientSecret = "EAJJamCdDW05nZOXBwpiEvvQAtb_sKROQWwMki6Tr_SVVJxuEXWVRgNLfuxhXfdWps8mCzJForxs8Qrq";
    public PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
    public PayPalHttpClient client = new PayPalHttpClient(environment);

    @Path("/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderPlace(@Context HttpHeaders headers, String jsonText){
        CartRetrieveRequestModel requestModel;
        OrderPlaceResponseModel responseModel;

        try{
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText,CartRetrieveRequestModel.class);
        }catch(JsonParseException e){
            responseModel = new OrderPlaceResponseModel(-3,"JSON Parse Exception.",null,null);
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }catch (JsonMappingException e) {
            responseModel = new OrderPlaceResponseModel(-2,"JSON Mapping Exception.",null,null);
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } catch (Exception e) {
            responseModel = new OrderPlaceResponseModel(-1,"Internal Server Error.",null,null);
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        String email = requestModel.getEmail();
        String query =  "SELECT JSON_ARRAYAGG(JSON_OBJECT('email', t.email, 'movie_id', t.movie_id, 'quantity', t.quantity, 'unit_price', t.unit_price, 'discount', t.discount)) as Items\n" +
                "FROM (SELECT c.email, c.movie_id, c.quantity, mp.unit_price, mp.discount\n"+
                "FROM cart as c\n"+
                "INNER JOIN movie_price as mp on c.movie_id = mp.movie_id\n"+
                "WHERE c.email LIKE ?) as t";
        ItemPartialModel[] items = null;
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                items = Utility.mapping(rs.getString("Items"),ItemPartialModel[].class);
            }else {
                responseModel = new OrderPlaceResponseModel(312,"Shopping cart item does not exist.",null,null);
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
            if(items==null||items.length==0){
                responseModel = new OrderPlaceResponseModel(312,"Shopping cart item does not exist.",null,null);
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        }catch (Exception e){
            e.printStackTrace();
            responseModel=new OrderPlaceResponseModel(-1,"Internal Server Error.",null,null);
            ServiceLogger.LOGGER.info("Getting cart fails");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        Order order = createPayPalOrder(items);
        if(order==null){
            responseModel = new OrderPlaceResponseModel(342,"Order creation failed.",null,null);
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email",headers.getHeaderString("email"));
            builder.header("session_id",headers.getHeaderString("session_id"));
            builder.header("transaction_id",headers.getHeaderString("transaction_id"));
            return builder.build();
        }

        String approve_url = null;
        for(LinkDescription link: order.links()){
            if(link.rel().equals("approve")){
                approve_url = link.href();
                break;
            }
        }
        String token = order.id();

        String querySale = "INSERT INTO sale(email, movie_id, quantity, sale_date) VALUES (?, ? ,? ,?)";
        String queryTransaction = "INSERT INTO transaction(sale_id, token) VALUES (?, ?)";
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(querySale, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(queryTransaction);
            ps1.setString(2,token);
            ResultSet rs;
            int last_id_inserted = 0;
            for (ItemPartialModel model: items){
                ps.setString(1,model.getEmail());
                ps.setString(2,model.getMovie_id());
                ps.setInt(3,model.getQuantity());
                ps.setDate(4,new Date(System.currentTimeMillis()));
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if(rs.next()){
                    last_id_inserted = rs.getInt(1);
                    ps1.setInt(1,last_id_inserted);
                    ps1.executeUpdate();
                }

                responseModel=new OrderPlaceResponseModel(3400,"Order placed successfully.",approve_url,token);
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email",headers.getHeaderString("email"));
                builder.header("session_id",headers.getHeaderString("session_id"));
                builder.header("transaction_id",headers.getHeaderString("transaction_id"));
                return builder.build();
            }
        }catch (Exception e){
            ServiceLogger.LOGGER.warning("error in inserting to 'sale'.");
            e.printStackTrace();
        }
        responseModel = new OrderPlaceResponseModel(-1,"Internal Server Error.",null,null);
        ServiceLogger.LOGGER.warning("Internal Server Error.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }

    @Path("/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderRetrieve(@Context HttpHeaders headers, String jsonText){
        CartRetrieveRequestModel requestModel;
        OrderRetrieveResponseModel responseModel;

        try{
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText,CartRetrieveRequestModel.class);
        }catch(JsonParseException e){
            responseModel = new OrderRetrieveResponseModel(-3,"JSON Parse Exception.",null);
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }catch (JsonMappingException e) {
            responseModel = new OrderRetrieveResponseModel(-2,"JSON Mapping Exception.",null);
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } catch (Exception e) {
            responseModel = new OrderRetrieveResponseModel(-1,"Internal Server Error.",null);
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        String email = requestModel.getEmail();

        String query =  "SELECT s.email, s.movie_id, s.quantity, s.sale_date, m.unit_price, m.discount, t.token\n"+
                "FROM sale as s\n"+
                "INNER JOIN movie_price as m on s.movie_id = m.movie_id\n"+
                "INNER JOIN transaction as t on s.sale_id = t.sale_id\n"+
                "WHERE s.email = ?";

        Map<String,ArrayList<ItemSaleModel>> transaction = new HashMap<>();
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String key = rs.getString("token");
                if(!transaction.containsKey(key)){
                    transaction.put(key, new ArrayList<>());
                }
                transaction.get(key).add(new ItemSaleModel(rs.getString("email"),
                        rs.getString("movie_id"),
                        rs.getInt("quantity"),
                        rs.getFloat("unit_price"),
                        rs.getFloat("discount"),
                        rs.getDate("sale_date").toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
            responseModel = new OrderRetrieveResponseModel(-1,"Internal Server Error.",null);
            ServiceLogger.LOGGER.warning("Internal Server Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        Map<String,ItemSaleModel[]> foundTransaction = new HashMap<>();
        transaction.forEach((k,v)->{
            ItemSaleModel[] item = v.toArray(new ItemSaleModel[v.size()]);
            foundTransaction.put(k,item);
        });
        ArrayList<TransactionModel> transactionModels = new ArrayList<>();
        foundTransaction.forEach((k,v)->{
            try{
                OrdersGetRequest request = new OrdersGetRequest(k);
                HttpResponse<Order> response = client.execute(request);
                transactionModels.add(new TransactionModel(response.result().purchaseUnits().get(0).payments().captures().get(0).id(),
                        response.result().status(),
                        new AmountModel(response.result().purchaseUnits().get(0).payments().captures().get(0).sellerReceivableBreakdown().grossAmount().value(), response.result().purchaseUnits().get(0).payments().captures().get(0).sellerReceivableBreakdown().grossAmount().currencyCode()),
                        new TransactionFeeModel(response.result().purchaseUnits().get(0).payments().captures().get(0).sellerReceivableBreakdown().paypalFee().value(), response.result().purchaseUnits().get(0).payments().captures().get(0).sellerReceivableBreakdown().paypalFee().currencyCode()),
                        response.result().createTime(),
                        response.result().updateTime(),
                        v));
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        TransactionModel[] temp = transactionModels.toArray(new TransactionModel[0]);
        responseModel = new OrderRetrieveResponseModel(3410,"Orders retrieved successfully.",temp);
        Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id",headers.getHeaderString("transaction_id"));
        return builder.build();
    }

    @Path("/complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderComplete(@Context HttpHeaders headers,
                                  @QueryParam("token") String token,
                                  @QueryParam("payer_id") String payer_id){
        CartInsertResponseModel responseModel;
        String query = "SELECT * FROM transaction WHERE token LIKE ? LIMIT 1";
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,token);
            ResultSet  rs = ps.executeQuery();
            if(!rs.next()){
                responseModel = new CartInsertResponseModel(3421,"Token not found.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                return builder.build();
            }
        }catch (Exception e){
            e.printStackTrace();
            responseModel = new CartInsertResponseModel(-1,"Internal server error.");
            Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
            return builder.build();
        }
        Order order = null;
        OrdersCaptureRequest request = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> response = client.execute(request);
            order = response.result();
        } catch (IOException ioe) {
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
                he.headers().forEach(x -> System.out.println(x + " :" + he.headers().header(x)));
                responseModel = new CartInsertResponseModel(-1,"Internal server error.");
                Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
                return builder.build();
            } else {
                ioe.printStackTrace();
                responseModel = new CartInsertResponseModel(-1,"Internal server error.");
                Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
                return builder.build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new CartInsertResponseModel(-1,"Internal server error.");
            Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
            return builder.build();
        }
        if(order == null){
            responseModel = new CartInsertResponseModel(3422,"Order can not be completed.");
            Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
            return builder.build();
        }
        String captureID = order.purchaseUnits().get(0).payments().captures().get(0).id();
        String query1 = "UPDATE transaction SET capture_id = ? WHERE token LIKE ?";
        try {
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, captureID);
            ps1.setString(2, token);
            int row = ps1.executeUpdate();
            if(row>0){
                String query2 = "DELETE FROM cart WHERE email LIKE (SELECT s.email FROM sale as s INNER JOIN transaction as t on s.sale_id = t.sale_id WHERE t.token LIKE ? LIMIT 1)";
                PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
                ps2.setString(1,token);
                ps2.executeUpdate();
                responseModel = new CartInsertResponseModel(3420,"Order is completed successfully.");
                Response.ResponseBuilder builder=Response.status(Response.Status.OK).entity(responseModel);
                return builder.build();
            }
        }catch (Exception e){
            e.printStackTrace();
            responseModel = new CartInsertResponseModel(-1,"Internal server error.");
            Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
            return builder.build();
        }

        responseModel = new CartInsertResponseModel(-1,"Internal Server Error.");
        Response.ResponseBuilder builder=Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel);
        return builder.build();
    }

    private Order createPayPalOrder(ItemPartialModel[] models) {
        float total = 0.00f;
        Order order;
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        String scheme = BillingService.getServiceConfigs().getScheme();
        String hostName = BillingService.getServiceConfigs().getHostName();
        int port = BillingService.getServiceConfigs().getPort();
        String path = BillingService.getServiceConfigs().getPath();
//        "http://localhost:12345/api/billing/order/complete"
        ApplicationContext applicationContext = new ApplicationContext().returnUrl(scheme+hostName+":"+Integer.toString(port)+path+"/order/complete");
        orderRequest.applicationContext(applicationContext);
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        for (ItemPartialModel model : models) {
            total += model.getQuantity() * (model.getUnit_price() * (1 - model.getDiscount()));
        }
        purchaseUnits.add(new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(Double.toString(total))));
        orderRequest.purchaseUnits(purchaseUnits);
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        try {
            HttpResponse<Order> response = client.execute(request);
            order = response.result();
            return order;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            if (ioe instanceof HttpException) {
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
                he.headers().forEach(x -> System.out.println(x + " :" + he.headers().header(x)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
