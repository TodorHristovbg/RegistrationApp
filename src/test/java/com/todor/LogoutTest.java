package com.todor;


import java.io.StringWriter;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class LogoutServletTest {

    private TestableLogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{

        servlet = new TestableLogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void logoutInvalidatesExistingSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);

        servlet.callDoPost(request,response);
        verify(session).invalidate();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void logoutWithNoSession() throws IOException{
      
        when(request.getSession(false)).thenReturn(null);
        assertDoesNotThrow(() -> servlet.callDoPost(request,response));
        verify(response).setStatus(HttpServletResponse.SC_OK);

    }

}