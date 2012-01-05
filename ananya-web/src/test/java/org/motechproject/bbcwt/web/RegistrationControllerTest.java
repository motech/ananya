package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.bbcwt.repository.AllRecordings;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(flwService, allRecordings);
    }

    @Test
    public void shouldReturnRegistrationCallFlowIfFLWIsNotRegistered() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("session.callerid")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.UNREGISTERED);

        ModelAndView modelAndView = controller.getLandingPage(request);

        ModelMap modelMap = modelAndView.getModelMap();
        assertEquals("/vxml/register/", modelMap.get("rendering_Page"));
        assertEquals("caller-landing-page", modelAndView.getViewName());
    }

    @Test
    public void shouldReturnChoiceCallFlowIfFLWIsRegistered() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session.connection.remote.uri")).thenReturn("991");
        when(flwService.getStatus("991")).thenReturn(FrontLineWorkerStatus.REGISTERED);

        ModelAndView modelAndView = controller.getLandingPage(request);

        ModelMap modelMap = modelAndView.getModelMap();
        assertEquals("/vxml/menu/", modelMap.get("rendering_Page"));
        assertEquals("caller-landing-page", modelAndView.getViewName());
    }

    @Test
    public void shouldReturnRegistrationPage() {
        ModelAndView modelAndView = controller.getRegisterPage();
        assertEquals("register-flw", modelAndView.getViewName());
    }

    @Test
    public void shouldReturnMenuPage() {
        ModelAndView modelAndView = controller.getMenuPage();
        assertEquals("top-menu", modelAndView.getViewName());
    }


}
        