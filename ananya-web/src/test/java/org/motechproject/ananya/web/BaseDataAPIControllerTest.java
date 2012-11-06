package org.motechproject.ananya.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.exception.ValidationException;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BaseDataAPIControllerTest {
    @Mock
    private HttpServletResponse response;

    @Test
    public void shouldReturnBadRequestForValidationExceptions() {
        BaseDataAPIController baseDataAPIController = new BaseDataAPIController() {
        };
        baseDataAPIController.handleException(new ValidationException("1234"), response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldReturnInternalServerErrorForExceptionsOtherThanValidationException() {
        BaseDataAPIController baseDataAPIController = new BaseDataAPIController() {
        };
        baseDataAPIController.handleException(new RuntimeException("1234"), response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
