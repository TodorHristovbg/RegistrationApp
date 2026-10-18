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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoginServletTest {

    private TestableLoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MyUserRepository userRepository;
    private HttpSession session;
    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{

        userRepository = mock(MyUserRepository.class);
        servlet = new TestableLoginServlet(userRepository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
       
        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void loginWithEmptyEmail() throws IOException{
        
        when(request.getParameter("email")).thenReturn("");
        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("Please fill in all fields"));
    }
    @Test
    void loginWithNullEmail() throws IOException{
        
        when(request.getParameter("email")).thenReturn(null);
        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("Please fill in all fields"));
    }

    @Test
    void loginWithEmptyPassword() throws IOException{
        
        when(request.getParameter("password")).thenReturn("");
        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("Please fill in all fields"));
    }
    @Test
    void loginWithNullPassword() throws IOException{
        
        when(request.getParameter("password")).thenReturn(null);
        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("Please fill in all fields"));
    }

    @ParameterizedTest
    @CsvSource({
        "plaintext, password",
        "user@denied, password",
        "userwithshortdomain@domain.c, password",
        ".userwithleadingdot@abv.bg, password",
        "userwithtwodots..name@abv.bg, password"
    })
    void LoginWithInvalidEmail(String email, String password) throws Exception{
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);

        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        assertTrue(responseBody.toString().contains("Please fill in a valid email address"));
    }

    @Test
    void LoginSuccess() throws Exception {
    when(request.getParameter("email")).thenReturn("todor@abv.bg");
    when(request.getParameter("password")).thenReturn("Tod");

    when(userRepository.DoesUserExistForEmail("todor@abv.bg")).thenReturn(true);
    when(userRepository.CheckCredentials("todor@abv.bg",Integer.toString("Tod".hashCode()))).thenReturn(1);

    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute("userId")).thenReturn(1);

    servlet.callDoPost(request, response);

    verify(userRepository).DoesUserExistForEmail("todor@abv.bg");
    verify(userRepository).CheckCredentials("todor@abv.bg",Integer.toString("Tod".hashCode()));

    verify(request).changeSessionId();
    verify(session).setAttribute("userId", 1);
    verify(session).setMaxInactiveInterval(30 * 60);
    verify(response).setStatus(HttpServletResponse.SC_OK);

    assertTrue(responseBody.toString().contains("Login Successful"));
    }

    @Test
    void LoginEmailNotRegistered() throws Exception {
    when(request.getParameter("email")).thenReturn("unknown@bg.bg");
    when(request.getParameter("password")).thenReturn("Tod");
    when(userRepository.DoesUserExistForEmail("unknown@bg.bg")).thenReturn(false);

    servlet.callDoPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    assertTrue(responseBody.toString().contains("There is no user registered with this email"));
    }
    @Test
    void LoginIncorrectPassword() throws Exception {

    when(request.getParameter("email")).thenReturn("tod@abv.bg");
    when(request.getParameter("password")).thenReturn("wrongpassword");
    when(userRepository.DoesUserExistForEmail("tod@abv.bg")).thenReturn(true);
    when(userRepository.CheckCredentials("tod@abv.bg",Integer.toString("wrongpassword".hashCode()))).thenReturn(-1);

    servlet.callDoPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    assertTrue(responseBody.toString().contains("Incorrect password!"));
    }
    @Test
    void LoginDatabaseErrorCheckingCredentials() throws Exception {
    when(request.getParameter("email")).thenReturn("abv@abv.bg");
    when(request.getParameter("password")).thenReturn("Tod");

    when(userRepository.DoesUserExistForEmail("abv@abv.bg")).thenReturn(true);

    when(userRepository.CheckCredentials("abv@abv.bg",Integer.toString("Tod".hashCode())))
    .thenThrow(new SQLException("Database error"));

    servlet.callDoPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

    assertTrue(responseBody.toString().contains("Login failed"));
    }
}