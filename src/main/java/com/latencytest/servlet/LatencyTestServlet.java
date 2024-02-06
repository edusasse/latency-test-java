package com.latencytest.servlet;

import com.microsoft.applicationinsights.TelemetryClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * LatencyTestServlet
 */
@WebServlet(name = "LatencyTestServlet", urlPatterns = { "/latencytest" }, loadOnStartup = 1)
public class LatencyTestServlet extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LatencyTestServlet.class);

    static final TelemetryClient telemetryClient = new TelemetryClient();

    @Override
    public void init() {
        LOGGER.info("LatencyTestServlet initialized on app startup.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServlet.doGet start");

        latencyTest(req, resp);

        telemetryClient.trackEvent("LatencyTestServlet.doGet end");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        telemetryClient.trackEvent("LatencyTestServlet.doPost start");

        testLatency(req, resp);

        telemetryClient.trackEvent("LatencyTestServlet.doPost end");
    }

    private void testLatency(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String urlParam = req.getParameter("url");
        if ((urlParam == null) || (urlParam != null && urlParam.trim().length() == 0)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing.");
            return;
        }

        try {
            // Perform a GET call to the provided URL
            long startTime = System.currentTimeMillis();
            URL url = new URL(urlParam);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;

                telemetryClient.trackEvent("LatencyTestServlet.latency result [" + latency + "]");

                resp.setContentType("text/plain");
                resp.getWriter().write(String.valueOf(latency));
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch URL.");
            }
        } catch (MalformedURLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL.");
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while fetching URL.");
        }
    }

    private void latencyTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Respond with a simple OK
        resp.getWriter().write("OK");
    }
}