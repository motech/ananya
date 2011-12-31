package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class RegistrationControllerTest {

    private RegistrationController controller;
    @Mock
    private FrontLineWorkerService flwService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(flwService);
    }

    @Test
    public void shouldReturnRegistrationCallFlowIfFLWIsNotRegistered() {
        when(request.getParameter("msisdn")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.UNREGISTERED);

        ModelAndView modelAndView = controller.callFlow(request, response);

        assertEquals("register-flw", modelAndView.getViewName());
        verify(response).setContentType("text/xml");
    }

    @Test
    public void shouldReturnChoiceCallFlowIfFLWIsRegistered() {
        when(request.getParameter("msisdn")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.REGISTERED);

        ModelAndView modelAndView = controller.callFlow(request, response);

        assertEquals("top-menu", modelAndView.getViewName());
        verify(response).setContentType("text/xml");
    }

}
    