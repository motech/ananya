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


//    @Test
//    public void shouldReturnLandingWithGotoBasedOnEntryPathVariable(){
//        ModelAndView modelAndView = landingController.entryRouter(request, "jobaid");
//        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
//        assertEquals("landing",modelAndView.getViewName());
//        assertEquals(contextAndVersion() + "/vxml/jobaid/enter", nextFlow);
//    }

    private String contextAndVersion() {
        return "/ananya/" + URL_VERSION;
    }

    @Test
    public void shouldPopulateRenderingPageToJobAidForJobAidCallFlow() {

        ModelAndView modelAndView = landingController.enterJobAid(request);

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");
        String entryJs = (String) modelAndView.getModel().get("entryJs");

        assertEquals(contextAndVersion() + "/vxml/jobaid.vxml", nextFlow);
        assertEquals(contextAndVersion() + "/vxml/register.vxml", registerFlow);
        assertEquals("'" + contextAndVersion() + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
        assertEquals(contextAndVersion() + "/js/entry/controller.js", entryJs);
        assertEquals("jobaid-entry",modelAndView.getViewName());
    }

    @Test
    public void shouldPopulateRenderingPageToCertificationCourseForCourseCallFlow() {
        ModelAndView modelAndView = landingController.enterCertificateCourse(request);

        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        String registerFlow = (String) modelAndView.getModel().get("registerFlow");
        String callerData = (String) modelAndView.getModel().get("callerData");
        String entryJs = (String) modelAndView.getModel().get("entryJs");
        String contextAndVersionPassedToTemplate = (String) modelAndView.getModel().get("contextPathWithVersion");

        assertEquals(contextAndVersion() + "/vxml/certificatecourse.vxml", nextFlow);
        assertEquals(contextAndVersion() + "/vxml/register.vxml", registerFlow);
        assertEquals("'" + contextAndVersion() + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri", callerData);
        assertEquals(contextAndVersion() + "/js/entry/controller.js", entryJs);
        assertEquals(contextAndVersion(), contextAndVersionPassedToTemplate);
        assertEquals("certificate-course-entry",modelAndView.getViewName());
    }
}
