package com.todor.servlets;
import com.todor.db.*;
import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/updatepassword")
public class UpdatePassword extends HttpServlet {

    private final MyUserRepository userRepository;

    public UpdatePassword() {
    this(new MyUserRepository());
    }

    public UpdatePassword(MyUserRepository userRepository) {
    this.userRepository = userRepository;
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
          response.setContentType("text/plain");
          response.setCharacterEncoding("UTF-8");
          var out = response.getWriter();
          
          HttpSession session = request.getSession(false);
          if(session == null || session.getAttribute("userId") == null || (Integer)session.getAttribute("userId")<=0) {
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               out.println("Login first");
               return;
          }
          
          int userid = (Integer) session.getAttribute("userId");
          String new_password = request.getParameter("new_password");
          if(new_password==null || new_password.trim().isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("password cannot be empty");
            return;
          }


          try{
               Class.forName("org.postgresql.Driver");
               
               try{
               boolean updated = userRepository.updatePassword(userid, Integer.toString(new_password.hashCode()));
               if(!updated){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Couldnt find user with this session ID");
                return;
               }
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("Updated Password");
                return ;

                } catch (SQLException e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("UpdatePassword failed due to a DB error");
                }
                
          } catch(Exception e ){
               response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
               response.getWriter().println("UpdatePassword failed");
               }
            }
        }
