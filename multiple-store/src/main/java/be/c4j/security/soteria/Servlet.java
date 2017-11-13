/*
 * Copyright 2017 Rudy De Busscher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.c4j.security.soteria;

import javax.annotation.security.DeclareRoles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Test Servlet that prints out the name of the authenticated caller and whether
 * this caller is in any of the roles {foo, bar, kaz}
 */

@DeclareRoles({"group1", "group2", "group3"})
@WebServlet("/servlet")
public class Servlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().write("This is a servlet \n");

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("web username: " + webName + "\n");

        response.getWriter().write("web user has role \"group1\": " + request.isUserInRole("group1") + "\n");
        response.getWriter().write("web user has role \"group2\": " + request.isUserInRole("group2") + "\n");
        response.getWriter().write("web user has role \"group3\": " + request.isUserInRole("group3") + "\n");
    }

}
