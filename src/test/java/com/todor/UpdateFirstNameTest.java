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

class UpdateFirstNameTest {

    private TestableUpdateFirstName servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private MyUserRepository userRepository;

    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{
        userRepository = mock(MyUserRepository.class);
        servlet = new TestableUpdateFirstName(userRepository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void UpdateFirstNameWithoutSession() throws IOException{
      
        when(request.getSession(false)).thenReturn(null);
        assertDoesNotThrow(() -> servlet.callDoPost(request,response));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));

    }

    @Test
    void UpdateFirstNameWithNullUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }

    @Test
    void UpdateFirstNameWithZeroUserIdinSession() throws IOException{
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(0);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }

    @Test
    void UpdateFirstNameWithNegativeUserIdinSession() throws IOException{
        
       when(request.getSession(false)).thenReturn(session);
       when(session.getAttribute("userId")).thenReturn(-1);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("Login first"));
    }


    @ParameterizedTest
    @CsvSource(value = {"''","' '","'   '","NULL"},nullValues = {"NULL"})
      void UpdateFirstNameWithEmptyNewName(String new_first_name) throws IOException{
        
       when(request.getSession(false)).thenReturn(session);
       when(session.getAttribute("userId")).thenReturn(1);
       when(request.getParameter("new_first_name")).thenReturn(new_first_name);

        servlet.callDoPost(request,response);
        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("First name cannot be empty"));
    }
    
    @Test
    void UpdateFirstNameSuccess() throws Exception {

    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_first_name")).thenReturn("Tod");

    when(userRepository.updateFirstName(1, "Tod")).thenReturn(true);

    servlet.callDoPost(request, response);

    verify(userRepository).updateFirstName(1, "Tod");
    verify(response).setStatus(HttpServletResponse.SC_OK);
    assertTrue(responseBody.toString().contains("Updated First Name to Tod"));
    }
    
    @Test
    void UpdateFirstNameUserNotFound() throws Exception {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_first_name")).thenReturn("Tod");

    when(userRepository.updateFirstName(1, "Tod")).thenReturn(false);

    servlet.callDoPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    assertTrue(responseBody.toString()
            .contains("Couldnt find user with this session ID"));
}
    @Test
    void UpdateFirstNameDatabaseError() throws Exception {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);
    when(request.getParameter("new_first_name")).thenReturn("John");

    when(userRepository.updateFirstName(1, "John")).thenThrow(new SQLException("Database error"));

    servlet.callDoPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    assertTrue(responseBody.toString().contains("UpdateFirstName failed due to a DB error"));
}
}