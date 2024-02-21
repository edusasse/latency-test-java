package com.latencytest.servlet;

import com.microsoft.applicationinsights.TelemetryClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.TimeValue;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;


/**
 * LatencyTestServlet
 */
@WebServlet(name = "LatencyTestServletApacheHttp", urlPatterns = { "/latencytest_v2" }, loadOnStartup = 1)
public class LatencyTestServletApacheHttp extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServletApacheHttp.class);

    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    private static CloseableHttpClient httpClient = null;

    static {
        // Configure connection pool settings
        connManager.setMaxTotal(200); // Maximum total connections
        connManager.setDefaultMaxPerRoute(10); // Maximum connections per route
        connManager.setValidateAfterInactivity(TimeValue.ofMilliseconds(1000)); // Validate connections after 1 second of inactivity
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
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

                try (ClassicHttpResponse response = httpClient.execute(httpGet)) {
                    if (response.getCode() == HttpStatus.SC_OK) {
                        long endTime = System.currentTimeMillis();
                        long latency = endTime - startTime;
                        totalLatency += latency;
                        latenciesArray.put(latency);
                    } else {
                        // Handle error
                        EntityUtils.consumeQuietly(response.getEntity()); // Ensure entity is consumed
                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch URL.");
                        return;
                    }
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