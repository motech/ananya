package org.motechproject.ananya.web;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseAnanyaController {

    private static Logger log = LoggerFactory.getLogger(BaseAnanyaController.class);

    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    String handleException(final Exception exception, HttpServletResponse response) {
        log.error(exception.getClass().getName(), exception);
        response.setStatus(HttpServletResponse.SC_OK);
        return "var ananyaResponse = \"ANANYA_ERROR\";";
    }

    private String getExceptionString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ExceptionUtils.getMessage(ex));
        sb.append(ExceptionUtils.getStackTrace(ex));
        sb.append(ExceptionUtils.getRootCauseMessage(ex));
        sb.append(ExceptionUtils.getRootCauseStackTrace(ex));
        return sb.toString();
    }
}
