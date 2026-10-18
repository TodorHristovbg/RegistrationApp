package com.todor;

import com.todor.db.MyUserRepository;
import com.todor.servlets.LoadProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

class TestableLoadProfileServlet extends LoadProfile {
   
    public TestableLoadProfileServlet(MyUserRepository userRepository) {
        super(userRepository);
    }
    protected void callDoGet(
      HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
        super.doGet(request, response);
     }
}
