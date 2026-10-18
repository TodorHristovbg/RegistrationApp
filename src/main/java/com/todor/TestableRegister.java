package com.todor;
import com.todor.db.MyUserRepository;

import com.todor.servlets.Register;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

class TestableRegisterServlet extends Register {

   TestableRegisterServlet(MyUserRepository userRepository) {
        super(userRepository);
    }


    void callDoPost(
      HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
        super.doPost(request, response);
     }
}
