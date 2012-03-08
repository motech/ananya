package org.motechproject.ananya.web;

import org.apache.log4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Log4JContextSettingInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String callId = request.getParameter("callId");
        if(callId!=null) {
            MDC.put("callId", callId);
        }
        return true;
    }
}