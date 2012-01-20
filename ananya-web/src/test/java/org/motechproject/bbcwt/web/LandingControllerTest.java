package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class LandingControllerTest {

    private LandingController landingController;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp(){
        initMocks(this);
        landingController = new LandingController();
    }

    @Test
    public void shouldPopulateRenderingPageToJobAidForJobAidCallFlow(){
        ModelAndView modelAndView = landingController.getLandingPage("jobaid");
        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        assertEquals("/vxml/jobaid.vxml",nextFlow);
    }

    @Test
    public void shouldPopulateRenderingPageToCertificationCourseForCourseCallFlow(){
        ModelAndView modelAndView = landingController.getLandingPage("certificationCourse");
        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        assertEquals("/vxml/certificationCourse.vxml",nextFlow);
    }
}
