package org.motechproject.ananya.web.interceptors;

import org.motechproject.ananya.web.annotations.Authenticated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private final static Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private Properties apiKeys;
    private final String unauthorizedErrorMessage = "API key does not match.";


    @Autowired
    public AuthenticationInterceptor(@Value("#{apiKeys}") Properties apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass().getAnnotation(Authenticated.class) == null)
            return true;

        String apiKey = request.getHeader("APIKey");

        if (apiKey == null || !apiKeys.containsValue(apiKey)) {
            log.error("Authentication failed with API key : " + apiKey);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, unauthorizedErrorMessage);
            return false;
        }

        log.info("Authenticated " + apiKey);
        return true;
    }
}
