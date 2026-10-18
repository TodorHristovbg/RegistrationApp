package com.todor;

import com.todor.db.MyUserRepository;
import com.todor.servlets.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

class TestableLoginServlet extends Login {
   
   TestableLoginServlet(MyUserRepository userRepository) {
        super(userRepository);
    }

    void callDoPost(
      HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
        super.doPost(request, response);
     }
}
