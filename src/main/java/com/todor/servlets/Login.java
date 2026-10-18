package com.todor.servlets;
import com.todor.db.*;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/login")
public class Login extends HttpServlet {

    private final MyUserRepository userRepository;

    public Login() {
    this(new MyUserRepository());
    }

    public Login(MyUserRepository userRepository) {
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
        var out = response.getWriter();

        if(password == null
        || password.isBlank()
        || email == null
        || email.isBlank()
        ) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Please fill in all fields"
            + email +"\n"
            + password +"\n"
            );
            return;
        }

        String emailregex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern p = Pattern.compile(emailregex);
        if(!p.matcher(email).matches()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Please fill in a valid email address");
            return;
        }


          try{
               Class.forName("org.postgresql.Driver");

            if(!userRepository.DoesUserExistForEmail(email)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("There is no user registered with this email");
            return ;
            }

            int result = userRepository.CheckCredentials(email, Integer.toString(password.hashCode()));
            if(result==-1){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("Incorrect password!");
                return;
            }
            HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setAttribute("userId", result);
            session.setMaxInactiveInterval(30 * 60);

            response.setStatus(HttpServletResponse.SC_OK);
            out.println("Login Successful");
            
          } catch(Exception e ){
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               out.println("Login failed");
               }
          }
     }

