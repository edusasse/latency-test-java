package com.latencytest.servlet;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LatencyTestServletWildflyHttpClientWithPool", urlPatterns = {"/latencytest_v4"}, loadOnStartup = 1)
public class LatencyTestServletWildflyHttpClientWithApacheHttp extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServletWildflyHttpClientWithApacheHttp.class);

    @Override
    public void init() {
        LOGGER.info("LatencyTestServletWildflyHttpClientWithApacheHttp4 initialized on app startup.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        latencyTest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        testLatency(req, resp);
    }

    private void testLatency(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String urlParam = req.getParameter("url");
        final int numCalls = Integer.parseInt(req.getParameter("numCalls"));
        final int delayInMillis = Integer.parseInt(req.getParameter("delayInMillis")); // Delay between each call

        if ((urlParam == null) || (urlParam != null && urlParam.trim().length() == 0)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing.");
            return;
        }

        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, new PoolingHttpClientConnectionManager());
        clientConfig.connectorProvider(new ApacheConnectorProvider());
        final Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();

        try {
            JSONArray latenciesArray = new JSONArray();
            long totalLatency = 0;

            for (int i = 0; i < numCalls; i++) {
                long startTime = System.currentTimeMillis();

                // Building the request with custom headers
                final Invocation.Builder builder = client.target(urlParam).request();
				builder.header("Max-Forwards", "10");
				builder.header("Request-Id", "|5e9da3a70f8974b89b8f13979db4563a.ef7194154f279400.");
				builder.header("Request-Context", "appId=cid-v1:5fed860a-92e1-4340-9cdd-f3af6f8d6fad");
				builder.header("X-ARR-LOG-ID", "1abbea78-8053-4464-ba2e-2a3cd0a8c42d");
				builder.header("CLIENT-IP", "51.136.123.140:2432");
				builder.header("X-Client-IP", "51.136.123.140");
				builder.header("DISGUISED-HOST", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				builder.header("X-SITE-DEPLOYMENT-ID", "gato-i-weu-001-dt-main-app-java");
				builder.header("WAS-DEFAULT-HOSTNAME", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				builder.header("X-Forwarded-For", "51.136.123.140:2432");
				builder.header("X-Original-URL", "/latencytest_v1");
				builder.header("X-WAWS-Unencoded-URL", "/latencytest_v1");
				builder.header("X-Client-Port", "2432");

                final Response response = builder.get();
                    long endTime = System.currentTimeMillis();
                    long latency = endTime - startTime;
                    totalLatency += latency;
                    latenciesArray.put(latency);
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch URL.");
                    return;
                }

                Thread.sleep(delayInMillis); // Adding delay between calls
            }

            long averageLatency = totalLatency / numCalls;

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("latencies", latenciesArray);
            jsonResponse.put("average_latency", averageLatency);

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid delay value.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while fetching URL.");
        } finally {
            client.close();
        }
    }

    private void latencyTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Respond with a simple OK
        resp.getWriter().write("OK");
    }
}
