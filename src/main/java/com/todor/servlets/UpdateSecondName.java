package com.todor.servlets;
import com.todor.db.*;
import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/updatesecondname")
public class UpdateSecondName extends HttpServlet {

    private final MyUserRepository userRepository;

    public UpdateSecondName() {
    this(new MyUserRepository());
    }

    public UpdateSecondName(MyUserRepository userRepository) {
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
          String new_second_name = request.getParameter("new_second_name");
          if(new_second_name==null || new_second_name.trim().isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("second name cannot be empty");
            return;
          }


           try{
               Class.forName("org.postgresql.Driver");
               
               try{
               boolean updated = userRepository.updateSecondName(userid, new_second_name);
               if(!updated){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Couldnt find user with this session ID");
                return;
               }
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("Updated second Name to "+ new_second_name);
                return ;

                } catch (SQLException e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("UpdateSecondName failed due to a DB error");
                }
                
          } catch(Exception e ){
               response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
               response.getWriter().println("UpdateSecondName failed");
               }
            }
        }
        




