package com.todor;

import com.todor.servlets.Logout;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
class TestableLogoutServlet extends  Logout{
    void callDoPost(
      HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
        super.doPost(request, response);
     }
}