package edu.uci.ics.weiched.service.gateway.threadpool;

import edu.uci.ics.weiched.service.gateway.GatewayService;
import edu.uci.ics.weiched.service.gateway.logger.ServiceLogger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process(ClientRequest request, Connection connection) {
        if (request.getMethod().toString().equals("GET")){
            ServiceLogger.LOGGER.info("This is a GET request\n "+request.getURI()+request.getEndpoint());
            Client client = ClientBuilder.newClient();
            client.register(JacksonFeature.class);

            WebTarget webTarget = client.target(request.getURI()).path(request.getEndpoint());
            if(request.getQuery() != null) {
                for (Map.Entry<String, String> query : request.getQuery().entrySet()) {
                    webTarget = webTarget.queryParam(query.getKey(), query.getValue());
                }
            }
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("email", request.getEmail());
            invocationBuilder.header("session_id", request.getSession_id());
            invocationBuilder.header("transaction_id", request.getTransaction_id());
            Response response = invocationBuilder.get();
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO responses VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, request.getTransaction_id());
                ps.setString(2, request.getEmail());
                ps.setString(3, request.getSession_id());
                ps.setString(4, response.readEntity(String.class));
                ps.setInt(5, response.getStatus());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("insert into response failed");
            }
        }else if(request.getMethod().toString().equals("POST")){
            ServiceLogger.LOGGER.info("This is a POST request\n "+request.getURI()+request.getEndpoint());
            Client client = ClientBuilder.newClient();
            client.register(JacksonFeature.class);
            WebTarget webTarget = client.target(request.getURI()).path(request.getEndpoint());
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("email", request.getEmail());
            invocationBuilder.header("session_id", request.getSession_id());
            invocationBuilder.header("transaction_id", request.getTransaction_id());
            Response response = invocationBuilder.post(Entity.entity(request.getRequestBytes(), MediaType.APPLICATION_JSON));
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO responses VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, request.getTransaction_id());
                ps.setString(2, request.getEmail());
                ps.setString(3, request.getSession_id());
                ps.setString(4, response.readEntity(String.class));
                ps.setInt(5, response.getStatus());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("insert into response failed");
            }
        }
        ServiceLogger.LOGGER.info("insert into response succeeded");
    }

    @Override
    public void run() {
        while (true) {
            try {
                ClientRequest request = threadPool.takeRequest();
                Connection connection = GatewayService.getConnectionPoolManager().requestCon();
                process(request, connection);
                GatewayService.getConnectionPoolManager().releaseCon(connection);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
