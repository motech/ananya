package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class LandingControllerTest {

    private LandingController landingController;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
        landingController = new LandingController();
        when(request.getContextPath()).thenReturn("/ananya");
    }

    @Test
    public void shouldPopulateRenderingPageToJobAidForJobAidCallFlow() {

        ModelAndView modelAndView = landingController.getLandingPage(request, "jobaid");

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");

        assertEquals("/ananya/vxml/jobaid.vxml", nextFlow);
        assertEquals("/ananya/vxml/jobaid/register", registerFlow);
        assertEquals("'/ananya/dynamic/js/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
    }

    @Test
    public void shouldPopulateRenderingPageToCertificationCourseForCourseCallFlow() {
        ModelAndView modelAndView = landingController.getLandingPage(request, "certificationCourse");

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");

        assertEquals("/ananya/vxml/certificationCourse.vxml", nextFlow);
        assertEquals("/ananya/vxml/certificationCourse/register", registerFlow);
        assertEquals("'/ananya/dynamic/js/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
    }
}