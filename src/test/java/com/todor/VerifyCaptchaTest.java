package com.todor;

import com.todor.servlets.VerifyCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class VerifyCaptchaTest {

    private TestableVerifyCaptcha servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TestableVerifyCaptcha();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void correctCaptcha() throws Exception {
        when(request.getParameter("captcha")).thenReturn("secretpassword");

        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals("", responseBody.toString());
    }

    @Test
    void incorrectCaptcha() throws Exception {
        when(request.getParameter("captcha")).thenReturn("wronganswer");
        
        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Incorrect captcha"));
    }

    @Test
    void missingCaptcha() throws Exception {
        when(request.getParameter("captcha")).thenReturn(null);

        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Incorrect captcha"));
    }
}