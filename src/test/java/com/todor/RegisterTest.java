package com.todor;

import com.todor.db.MyUserRepository;

import java.io.StringWriter;
import java.sql.SQLException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RegisterServletTest {

    private TestableRegisterServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MyUserRepository userRepository;

    private StringWriter responseBody;

    @BeforeEach 
    void setup() throws Exception{
        userRepository = mock(MyUserRepository.class);
        servlet = new TestableRegisterServlet(userRepository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        
       
        responseBody = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @ParameterizedTest
    @CsvSource(
        value = {
        "'',second name,email,password",
        "'  ',second name,email,password",
        "NULL,second name,email,password",
        "first name,'',email,password",
        "first name,'   ',email,password",
        "first name,NULL,email,password",
        "first name,second name,'',password",
        "first name,second name,'  ',password",
        "first name,second name,NULL,password",
        "first name,second name,email,''",
        "first name,second name,email,'    '",
        "first name,second name,email,NULL"
        
    },
    nullValues = {"NULL"}
    )
    void RegisterWithEmptyField(String first_name, String second_name, String email, String password) throws Exception{
        when(request.getParameter("first_name")).thenReturn(first_name);
        when(request.getParameter("second_name")).thenReturn(second_name);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);

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
    void RegisterWithInvalidEmail(String email, String password) throws Exception{
        when(request.getParameter("first_name")).thenReturn("FirstName");
        when(request.getParameter("second_name")).thenReturn("SecondName");
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);

        servlet.callDoPost(request,response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        assertTrue(responseBody.toString().contains("Please fill in a valid email address"));
    }

    @Test
    void RegisterSuccess() throws Exception{
        when(request.getParameter("first_name")).thenReturn("FirstName");
        when(request.getParameter("second_name")).thenReturn("SecondName");
        when(request.getParameter("email")).thenReturn("user@abv.bg");
        when(request.getParameter("password")).thenReturn("Password");

        when(userRepository.registerNewUser(anyString(),anyString(),anyString(),anyString())).thenReturn(true);
        
        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseBody.toString().contains("Registration successful"));
    }
     void RegisterFail() throws Exception{
        when(request.getParameter("first_name")).thenReturn("FirstName");
        when(request.getParameter("second_name")).thenReturn("SecondName");
        when(request.getParameter("email")).thenReturn("user@abv.bg");
        when(request.getParameter("password")).thenReturn("Password");

        when(userRepository.registerNewUser(anyString(),anyString(),anyString(),anyString())).thenReturn(false);
        
        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("Registration failed"));
    }

    @Test
    void RegisterExistingEmail() throws Exception{
        when(request.getParameter("first_name")).thenReturn("FirstName");
        when(request.getParameter("second_name")).thenReturn("SecondName");
        when(request.getParameter("email")).thenReturn("user@abv.bg");
        when(request.getParameter("password")).thenReturn("Password");

        when(userRepository.registerNewUser(anyString(),anyString(),anyString(),anyString()))
        .thenThrow(new SQLException("Duplicate email", "23505"));
        
        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        assertTrue(responseBody.toString().contains("An account with this email already exists"));
    }

     @Test
    void GenericDBError() throws Exception{
        when(request.getParameter("first_name")).thenReturn("FirstName");
        when(request.getParameter("second_name")).thenReturn("SecondName");
        when(request.getParameter("email")).thenReturn("user@abv.bg");
        when(request.getParameter("password")).thenReturn("Password");

        when(userRepository.registerNewUser(anyString(),anyString(),anyString(),anyString()))
        .thenThrow(new SQLException("Duplicate email", "22023"));
        
        servlet.callDoPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("Registration failed due to a DB error"));
    }
}