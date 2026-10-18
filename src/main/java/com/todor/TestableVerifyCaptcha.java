package com.todor;

import com.todor.servlets.VerifyCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public class TestableVerifyCaptcha extends VerifyCaptcha {

    public void callDoPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        super.doPost(request, response);
    }
}
