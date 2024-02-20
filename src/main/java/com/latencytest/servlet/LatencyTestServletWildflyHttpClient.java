package com.latencytest.servlet;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * LatencyTestServlet
 */
@WebServlet(name = "LatencyTestServletWildflyHttpClient", urlPatterns = {"/latencytest_v3"}, loadOnStartup = 1)
public class LatencyTestServletWildflyHttpClient extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServletApacheHttp.class);

    private static ResteasyClient client = null;
    @Override
    public void init() {
        LOGGER.info("LatencyTestServletWildflyHttpClient initialized on app startup.");

        final ResteasyClientBuilder builder = new ResteasyClientBuilderImpl();
        client = builder
                .connectionPoolSize(20)
                .build();
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

        try {
            JSONArray latenciesArray = new JSONArray();
            long totalLatency = 0;

            for (int i = 0; i < numCalls; i++) {
                long startTime = System.currentTimeMillis();

                ResteasyWebTarget target = (ResteasyWebTarget) client.target(urlParam);
                Builder builder = target.request();

                Response response = builder.get();
                int responseCode = response.getStatus();
                if (responseCode == HttpServletResponse.SC_OK) {
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
        }
    }

    private void latencyTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Respond with a simple OK
        resp.getWriter().write("OK");
    }
}