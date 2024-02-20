package com.latencytest.servlet;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private final String baseUrl;
    private final BlockingQueue<HttpURLConnection> pool;

    public ConnectionPool(String baseUrl, int poolSize) {
        this.baseUrl = baseUrl;
        this.pool = new ArrayBlockingQueue<>(poolSize);
        initializePool(poolSize);
    }

    private void initializePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            try {
                URL url = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                pool.add(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HttpURLConnection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void releaseConnection(HttpURLConnection connection) {
        pool.add(connection);
    }
}
