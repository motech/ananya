package org.motechproject.ananya.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String LOGGED_IN_USER = "loggedInUser";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        request.getSession().setAttribute(LOGGED_IN_USER, user);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
