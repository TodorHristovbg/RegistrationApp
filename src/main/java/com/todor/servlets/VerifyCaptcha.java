package com.todor.servlets;

import java.io.IOException;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@WebServlet("/verifycaptcha")
public class VerifyCaptcha extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {

        Object captcha = request.getParameter("captcha");

        if (captcha==null || !captcha.equals("secretpassword")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Incorrect captcha");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);

     }
    
}
