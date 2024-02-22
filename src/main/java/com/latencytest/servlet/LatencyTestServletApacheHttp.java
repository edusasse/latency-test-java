package com.latencytest.servlet;

import com.microsoft.applicationinsights.TelemetryClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;


/**
 * LatencyTestServlet
 */
@WebServlet(name = "LatencyTestServletApacheHttp", urlPatterns = {"/latencytest_v2"}, loadOnStartup = 1)
public class LatencyTestServletApacheHttp extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServletApacheHttp.class);

    private static final CloseableHttpClient httpClient;

    static {
        // Configure HttpClient
        httpClient = HttpClients.createDefault();
    }

    static final TelemetryClient telemetryClient = new TelemetryClient();

    @Override
    public void init() {
        LOGGER.info("LatencyTestServletApacheHttp initialized on app startup.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServletApacheHttp.doGet start");

        latencyTest(req, resp);

        telemetryClient.trackEvent("LatencyTestServletApacheHttp.doGet end");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServletApacheHttp.doPost start");

        testLatency(req, resp);

        telemetryClient.trackEvent("LatencyTestServletApacheHttp.doPost end");
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

                HttpGet httpGet = new HttpGet(urlParam);
                httpGet.addHeader("Connection", "Keep-Alive"); // Set Keep-Alive header
				// Add extra headers
				httpGet.addHeader("Max-Forwards", "10");
				httpGet.addHeader("Request-Id", "|5e9da3a70f8974b89b8f13979db4563a.ef7194154f279400.");
				httpGet.addHeader("Request-Context", "appId=cid-v1:5fed860a-92e1-4340-9cdd-f3af6f8d6fad");
				httpGet.addHeader("X-ARR-LOG-ID", "1abbea78-9053-4464-ba2e-2a3cd0a8c42d");
				httpGet.addHeader("CLIENT-IP", "51.136.123.140:2432");
				httpGet.addHeader("X-Client-IP", "51.136.123.140");
				httpGet.addHeader("DISGUISED-HOST", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				httpGet.addHeader("X-SITE-DEPLOYMENT-ID", "gato-i-weu-001-dt-main-app-java");
				httpGet.addHeader("WAS-DEFAULT-HOSTNAME", "gato-i-weu-001-dt-main-app-java.azurewebsites.net");
				httpGet.addHeader("X-Forwarded-For", "51.136.123.140:2432");
				httpGet.addHeader("X-Original-URL", "/latencytest_v2");
				httpGet.addHeader("X-WAWS-Unencoded-URL", "/latencytest_v2");
				httpGet.addHeader("X-Client-Port", "2432");

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK && entity != null) {
                    long endTime = System.currentTimeMillis();
                    long latency = endTime - startTime;
                    totalLatency += latency;
                    latenciesArray.put(latency);
                    EntityUtils.consume(entity); // Ensure entity is consumed
                } else {
                    // Handle error
                    EntityUtils.consume(entity); // Ensure entity is consumed
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch URL.");
                    return;
                }

                Thread.sleep(delayInMillis);
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
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error [" + e.getMessage() + "]");
        }
    }

    private void latencyTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Respond with a simple OK
        resp.getWriter().write("OK");
    }
}
