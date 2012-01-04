package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.bbcwt.repository.AllRecordings;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.ui.ModelMap;
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
    @Mock
    private AllRecordings allRecordings;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(flwService, allRecordings);
    }

    @Test
    public void shouldReturnRegistrationCallFlowIfFLWIsNotRegistered() {
        when(request.getParameter("session.callerid")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.UNREGISTERED);

        ModelAndView modelAndView = controller.getLandingPage(request);

        ModelMap modelMap = modelAndView.getModelMap();
        assertEquals("/vxml/register/",modelMap.get("rendering_Page")) ;
        assertEquals("callerLandingPage", modelAndView.getViewName());
    }

    @Test
    public void shouldReturnChoiceCallFlowIfFLWIsRegistered() {
        when(request.getParameter("session.callerid")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.REGISTERED);

        ModelAndView modelAndView = controller.getLandingPage(request);

        ModelMap modelMap = modelAndView.getModelMap();
        assertEquals("/vxml/menu/",modelMap.get("rendering_Page")) ;
        assertEquals("callerLandingPage", modelAndView.getViewName());
    }
}
    