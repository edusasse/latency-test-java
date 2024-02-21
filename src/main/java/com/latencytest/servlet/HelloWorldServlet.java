package com.latencytest.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@WebServlet(name = "HelloWorldServlet", urlPatterns = { "/hello" })
public class HelloWorldServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int randomValue = new Random().nextInt(100);
        resp.setContentType("text/plain");
        resp.getWriter().write("Random Value: " + randomValue);
    }
}
