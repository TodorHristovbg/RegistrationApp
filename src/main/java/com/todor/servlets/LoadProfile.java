package com.todor.servlets;
import com.todor.db.*;

import java.io.IOException;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/loadprofile")
public class LoadProfile extends HttpServlet {
     
     private final MyUserRepository userRepository;

    public LoadProfile() {
    this(new MyUserRepository());
    }

    public LoadProfile(MyUserRepository userRepository) {
    this.userRepository = userRepository;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
          response.setContentType("text/plain");
          response.setCharacterEncoding("UTF-8");
          response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
          response.setHeader("Pragma", "no-cache");
          response.setDateHeader("Expires", 0);
          var out = response.getWriter();
          
          
          HttpSession session = request.getSession(false);
          if(session == null || session.getAttribute("userId") == null || (Integer)session.getAttribute("userId")<=0) {
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               out.println("Login first");
               return;
          }
          int userid = (Integer) session.getAttribute("userId");
          try{
          Class.forName("org.postgresql.Driver");

          try{
               User user = userRepository.findUserById(userid);
               if(user==null){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("User not found");
                    return;
               }

               response.setContentType("application/json");
               response.setCharacterEncoding("UTF-8");
               response.setStatus(HttpServletResponse.SC_OK);
                    
               ObjectMapper mapper = new ObjectMapper();
               Map<String,String> profile = Map.of(
               "first_name", user.getFirstName(),
               "second_name", user.getSecondName(),
               "email", user.getEmail()
               );
               mapper.writeValue(out, profile);
               return;
          } catch (SQLException e){
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          out.println("Could not load profile");
        }
     } catch (Exception e){
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          out.println("Could not load profile");
     }
     }
}    

   



