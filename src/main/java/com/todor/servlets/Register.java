package com.todor.servlets;
import com.todor.db.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/register")
public class Register extends HttpServlet {

    private final MyUserRepository userRepository;

    public Register() {
    this(new MyUserRepository());
    }

    public Register(MyUserRepository userRepository) {
    this.userRepository = userRepository;
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
          response.setContentType("text/plain");
          response.setCharacterEncoding("UTF-8");
          
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String first_name = request.getParameter("first_name");
        String second_name = request.getParameter("second_name");

        if(first_name == null
        || first_name.isBlank()
        || second_name == null
        || second_name.isBlank()
        || password == null
        || password.isBlank()
        || email == null
        || email.isBlank()
        ) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Please fill in all fields");
            return;
        }

        String emailregex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern p = Pattern.compile(emailregex);
        if(!p.matcher(email).matches()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Please fill in a valid email address");
            return;
        }
          try{
            Class.forName("org.postgresql.Driver");
            

            var out = response.getWriter();

            try {
                boolean registered = userRepository.registerNewUser(first_name, second_name, email, Integer.toString(password.hashCode()));
                if(!registered){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("Registration failed");
                    return;
                }

                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("Registration successful");
                
            } catch(SQLException e) {
                if(e.getSQLState().equals("23505")){
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.println("An account with this email already exists");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("Registration failed due to a DB error");
                return;
            }
          } catch(Exception e ){
               response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
               response.getWriter().println("Registration failed");
               }
          }
     }
