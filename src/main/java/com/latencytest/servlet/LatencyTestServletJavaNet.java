package com.latencytest.servlet;

import com.microsoft.applicationinsights.TelemetryClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;


/**
 * LatencyTestServlet
 */
@WebServlet(name = "LatencyTestServletJavaNet", urlPatterns = { "/latencytest_v1" }, loadOnStartup = 1)
public class LatencyTestServletJavaNet extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServletJavaNet.class);

    static final TelemetryClient telemetryClient = new TelemetryClient();

    private static final int POOL_SIZE = 1;
    private ConnectionPool connectionPool;

    @Override
    public void init() {
        LOGGER.info("LatencyTestServlet initialized on app startup."); // Initialize connection pool
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServletJavaNet.doGet start");

        latencyTest(req, resp);

        telemetryClient.trackEvent("LatencyTestServletJavaNet.doGet end");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServletJavaNet.doPost start");

        testLatency(req, resp);

        telemetryClient.trackEvent("LatencyTestServletJavaNet.doPost end");
    }

    private void testLatency(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String urlParam = req.getParameter("url");
        final int numCalls = Integer.parseInt(req.getParameter("numCalls"));
        final int delayInMillis = Integer.parseInt(req.getParameter("delayInMillis"));

        if ((urlParam == null) || (urlParam != null && urlParam.trim().length() == 0)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing.");
            return;
        }

        connectionPool = new ConnectionPool(urlParam, POOL_SIZE);

        try {
            JSONArray latenciesArray = new JSONArray();
            long totalLatency = 0;

            for (int i = 0; i < numCalls; i++) {
                long startTime = System.currentTimeMillis();
                URL url = new URL(urlParam);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
				
				 // Add extra headers
				connection.setRequestProperty("Max-Forwards", "10");
				connection.setRequestProperty("Request-Id", "|5e9da3a70f8974b89b8f13979db4563a.ef7194154f279400.");
				connection.setRequestProperty("Request-Context", "appId=cid-v1:5fed860a-92e1-4340-9cdd-f3af6f8d6fad");
				connection.setRequestProperty("X-ARR-LOG-ID", "1abbea78-8053-4464-ba2e-2a3cd0a8c42d");
				connection.setRequestProperty("CLIENT-IP", "51.136.123.140:2432");
				connection.setRequestProperty("X-Client-IP", "51.136.123.140");
				connection.setRequestProperty("DISGUISED-HOST", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				connection.setRequestProperty("X-SITE-DEPLOYMENT-ID", "gato-i-weu-001-dt-main-app-java");
				connection.setRequestProperty("WAS-DEFAULT-HOSTNAME", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				connection.setRequestProperty("X-Forwarded-For", "51.136.123.140:2432");
				connection.setRequestProperty("X-Original-URL", "/latencytest_v1");
				connection.setRequestProperty("X-WAWS-Unencoded-URL", "/latencytest_v1");
				connection.setRequestProperty("X-Client-Port", "2432");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
        } catch (MalformedURLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL.");
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while fetching URL.");
        } catch (InterruptedException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Thread interrupted.");
        }
    }

    private void latencyTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Respond with a simple OK
        resp.getWriter().write("OK");
    }
}