package com.todor;


import java.io.StringWriter;
import java.sql.SQLException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.todor.db.MyUserRepository;
import com.todor.db.User;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class LoadProfileServletTest {

    private TestableLoadProfileServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private MyUserRepository userRepository;

    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{

        userRepository = mock(MyUserRepository.class);
        servlet = new TestableLoadProfileServlet(userRepository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void LoadProfileWithNullUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);

        servlet.callDoGet(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }
    @Test
    void LoadProfileWithZeroUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(0);

        servlet.callDoGet(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }
    void LoadProfileWithNegativeUserIdinSession() throws IOException{
        
       when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(-1);

        servlet.callDoGet(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }

    @Test
    void LoadProfileWithoutSession() throws IOException{
      
        when(request.getSession(false)).thenReturn(null);
        assertDoesNotThrow(() -> servlet.callDoGet(request,response));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));

    }
    @Test
    void LoadProfileSuccess() throws Exception {

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);

        User user = new User(1,"tod","or","password","abv@abv.bg");

        when(userRepository.findUserById(1)).thenReturn(user);

        servlet.callDoGet(request, response);

        verify(userRepository).findUserById(1);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String body = responseBody.toString();

        assertTrue(body.contains("\"first_name\":\"tod\""));
        assertTrue(body.contains("\"second_name\":\"or\""));
        assertTrue(body.contains("\"email\":\"abv@abv.bg\""));
        assertFalse(body.contains("password"));
    }

    @Test
    void LoadProfileUserNotFound() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(userRepository.findUserById(1)).thenReturn(null);

        servlet.callDoGet(request, response);

        verify(userRepository).findUserById(1);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseBody.toString().contains("User not found"));
    }

    @Test
    void LoadProfileDatabaseError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);

        when(userRepository.findUserById(1)).thenThrow(new SQLException("Database error"));

        servlet.callDoGet(request, response);

        verify(userRepository).findUserById(1);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("Could not load profile"));
    }
}