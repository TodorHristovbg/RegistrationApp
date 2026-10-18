package com.todor;


import java.io.StringWriter;
import java.sql.SQLException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.todor.db.MyUserRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UpdatePasswordTest {

    private TestableUpdatePassword servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private MyUserRepository userRepository;

    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{

        userRepository = mock(MyUserRepository.class);
        servlet = new TestableUpdatePassword(userRepository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void UpdatePasswordWithoutSession() throws IOException{
      
        when(request.getSession(false)).thenReturn(null);
        assertDoesNotThrow(() -> servlet.callDoPost(request,response));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));

    }

    @Test
    void UpdatePasswordWithNullUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }

    @Test
    void UpdatePasswordWithZeroUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(0);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }

    @Test
    void UpdatePasswordWithNegativeUserIdinSession() throws IOException{
        
       when(request.getSession(false)).thenReturn(session);
       when(session.getAttribute("userId")).thenReturn(-1);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }


    @ParameterizedTest
    @CsvSource(value = {"''","' '","'   '","NULL"},nullValues = {"NULL"})
      void UpdatePasswordWithEmptyNewName(String new_password) throws IOException{
        
       when(request.getSession(false)).thenReturn(session);
       when(session.getAttribute("userId")).thenReturn(1);
       when(request.getParameter("new_password")).thenReturn(new_password);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("password cannot be empty"));
    }
    @Test
void UpdatePasswordSuccess() throws Exception {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_password")).thenReturn("Tod");

    String hashedPassword = hashPassword("Tod");

    when(userRepository.updatePassword(1, hashedPassword))
            .thenReturn(true);

    servlet.callDoPost(request, response);

    verify(userRepository).updatePassword(1, hashedPassword);
    verify(response).setStatus(HttpServletResponse.SC_OK);
    assertTrue(responseBody.toString().contains("Updated Password"));
}
    
    @Test
    void UpdatePasswordUserNotFound() throws Exception {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_password")).thenReturn("Tod");

    String hashedPassword = hashPassword("Tod");

    when(userRepository.updatePassword(1, hashedPassword)).thenReturn(false);

    servlet.callDoPost(request, response);

    verify(userRepository).updatePassword(1, hashedPassword);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    assertTrue(responseBody.toString().contains("Couldnt find user with this session ID"));
    }

    @Test
    void UpdatePasswordDatabaseError() throws Exception {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_password")).thenReturn("password");

    String hashedPassword = hashPassword("password");

    when(userRepository.updatePassword(1, hashedPassword))
            .thenThrow(new SQLException("Database error"));

    servlet.callDoPost(request, response);

    verify(userRepository).updatePassword(1, hashedPassword);
    verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    assertTrue(responseBody.toString().contains("UpdatePassword failed due to a DB error"));
    }
    
private String hashPassword(String password) {

    return Integer.toString(password.hashCode());
}
}