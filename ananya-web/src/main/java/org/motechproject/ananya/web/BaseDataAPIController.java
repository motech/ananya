package org.motechproject.ananya.web;

import org.hibernate.exception.ExceptionUtils;
import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.web.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseDataAPIController {
    private Logger logger = LoggerFactory.getLogger(BaseDataAPIController.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse handleException(final Exception exception, HttpServletResponse response) {
        logger.error(ExceptionUtils.getFullStackTrace(exception));
        String errorMessage="AN ERROR OCCURRED.";
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (exception instanceof ValidationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessage = exception.getMessage();
        }
        return BaseResponse.failure(errorMessage);
    }
}
