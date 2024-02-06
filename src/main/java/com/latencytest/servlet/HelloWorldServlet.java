package com.latencytest.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
