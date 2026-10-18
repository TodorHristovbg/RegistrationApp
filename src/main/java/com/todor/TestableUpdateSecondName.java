package com.todor;

import com.todor.db.MyUserRepository;
import com.todor.servlets.UpdateSecondName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

class TestableUpdateSecondName extends UpdateSecondName {
   
   TestableUpdateSecondName(MyUserRepository userRepository) {
        super(userRepository);
    }
    void callDoPost(
      HttpServletRequest request,
            HttpServletResponse response
     ) throws IOException {
        super.doPost(request, response);
     }
}
