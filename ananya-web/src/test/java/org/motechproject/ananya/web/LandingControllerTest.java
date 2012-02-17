package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class LandingControllerTest {

    private LandingController landingController;
    @Mock
    private HttpServletRequest request;
    private final static String URL_VERSION = "v1.1.1";

    @Before
    public void setUp() {
        initMocks(this);
        Properties properties = new Properties();
        properties.put("url.version", URL_VERSION);
        landingController = new LandingController(properties);
        when(request.getContextPath()).thenReturn("/ananya");
    }


    @Test
    public void shouldReturnLandingWithGotoBasedOnEntryPathVariable(){
        ModelAndView modelAndView = landingController.entryRouter(request, "jobaid");
        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        assertEquals("landing",modelAndView.getViewName());
        assertEquals("vxml/jobaid/enter/", nextFlow);
    }

    @Test
    public void shouldPopulateRenderingPageToJobAidForJobAidCallFlow() {

        ModelAndView modelAndView = landingController.enterJobAid(request);

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");
        String entryJs = (String) modelAndView.getModel().get("entryJs");

        assertEquals(URL_VERSION + "/vxml/jobaid.vxml", nextFlow);
        assertEquals(URL_VERSION + "/vxml/register.vxml", registerFlow);
        assertEquals("'" + URL_VERSION + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
        assertEquals(URL_VERSION + "/js/entry/controller.js", entryJs);
        assertEquals("jobaid-entry",modelAndView.getViewName());
    }

    @Test
    public void shouldPopulateRenderingPageToCertificationCourseForCourseCallFlow() {
        ModelAndView modelAndView = landingController.enterCertificateCourse(request);

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");
        String entryJs = (String) modelAndView.getModel().get("entryJs");

        assertEquals(URL_VERSION + "/vxml/certificatecourse.vxml", nextFlow);
        assertEquals(URL_VERSION + "/vxml/register.vxml", registerFlow);
        assertEquals("'" + URL_VERSION + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
        assertEquals(URL_VERSION + "/js/entry/controller.js", entryJs);
        assertEquals("certificate-course-entry",modelAndView.getViewName());
    }
}
