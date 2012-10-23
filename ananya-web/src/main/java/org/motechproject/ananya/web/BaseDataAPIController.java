package org.motechproject.ananya.web;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.web.exception.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseDataAPIController {
    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    String handleException(final Exception exception, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain");
        response.setStatus(exception instanceof  ValidationException ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ExceptionUtils.getStackTrace(exception);
    }
}
