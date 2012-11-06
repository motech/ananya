package org.motechproject.ananya.web;

import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.web.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseDataAPIController {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse handleException(final Exception exception, HttpServletResponse response) {
        response.setStatus(exception instanceof  ValidationException ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return BaseResponse.failure(exception.getMessage());
    }
}
