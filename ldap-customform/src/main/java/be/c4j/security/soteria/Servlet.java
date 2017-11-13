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

import java.io.IOException;

import javax.annotation.security.DeclareRoles;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.LdapIdentityStoreDefinition;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test Servlet that prints out the name of the authenticated caller and whether
 * this caller is in any of the roles {foo, bar, kaz}
 */
@CustomFormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage="/login.xhtml",
        errorPage=""
    )
)

//rudy/pw
//reza/secret1

@LdapIdentityStoreDefinition(
        url                = "ldap://localhost:10389",
        bindDn             = "uid=admin,ou=system",
        bindDnPassword     = "secret",
        callerSearchBase   = "ou=caller,dc=example,dc=com",
        callerSearchFilter = "(&(uid=%s)(objectClass=person))",
        groupSearchBase    = "ou=group,dc=example,dc=com"
)

@WebServlet("/servlet")
@DeclareRoles({ "devgroup1", "devgroup2", "devgroup3" })
@ServletSecurity(@HttpConstraint(rolesAllowed = "devgroup1"))
public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }
        
        response.getWriter().write(
                "<html><body> This is a servlet <br><br>\n" +
        
                    "web username: " + webName + "<br><br>\n" +
                            
                    "web user has role \"devgroup1\": " + request.isUserInRole("devgroup1") + "<br>\n" +
                    "web user has role \"devgroup2\": " + request.isUserInRole("devgroup2") + "<br>\n" +
                    "web user has role \"devgroup3\": " + request.isUserInRole("devgroup3") + "<br><br>\n" +

                        
                    "<form method=\"POST\">" +
                        "<input type=\"hidden\" name=\"logout\" value=\"true\"  >" +
                        "<input type=\"submit\" value=\"Logout\">" +
                    "</form>" +
                "</body></html>");
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("true".equals(request.getParameter("logout"))) {
            request.logout();
            request.getSession().invalidate();
        }
        
        doGet(request, response);
    }

}
