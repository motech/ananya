package org.motechproject.ananya.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class LogInterceptor extends HandlerInterceptorAdapter{
    private final static Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("Request START");
        LOG.info("Request URI - " + request.getRequestURI());
        LOG.info("Request parameters");

        Enumeration<String> requestKeys = request.getParameterNames();
        while (requestKeys.hasMoreElements()) {
            String key = requestKeys.nextElement();
            LOG.info(key + "  -->  " + request.getParameter(key));
        }

        //String callId = request.getParameter("callId");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.info(request.getRequestURI());
        // TODO: figure out how to dump response as a string in debug mode
//        LOG.debug(response.getOutputStream().toString());
        LOG.info("Request END");
    }
}
