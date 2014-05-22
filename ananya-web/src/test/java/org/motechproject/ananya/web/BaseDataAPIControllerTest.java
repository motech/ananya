package org.motechproject.ananya.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.web.response.BaseResponse;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BaseDataAPIControllerTest {
    @Mock
    private HttpServletResponse response;

    @Test
    public void shouldReturnBadRequestForValidationExceptions() {
        BaseDataAPIController baseDataAPIController = new BaseDataAPIController() {
        };
        String exceptionMessage = "1234";
        BaseResponse baseResponse = baseDataAPIController.handleException(new ValidationException(exceptionMessage), response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals(exceptionMessage, baseResponse.getDescription());
        assertEquals("ERROR", baseResponse.getStatus());
    }

    @Test
    public void shouldReturnInternalServerErrorForExceptionsOtherThanValidationException() {
        BaseDataAPIController baseDataAPIController = new BaseDataAPIController() {
        };
        BaseResponse baseResponse = baseDataAPIController.handleException(new RuntimeException("1234"), response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertEquals("AN ERROR OCCURRED.", baseResponse.getDescription());
        assertEquals("ERROR", baseResponse.getStatus());
    }
}
