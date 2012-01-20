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
        ModelAndView modelAndView = landingController.getLandingPage(request, "jobaid");
        String renderingPage = (String) modelAndView.getModel().get("renderingPage");
        assertEquals("/vxml/jobaid.vxml",renderingPage);
    }

    @Test
    public void shouldPopulateRenderingPageToCertificationCourseForCourseCallFlow(){
        ModelAndView modelAndView = landingController.getLandingPage(request, "certificationCourse");
        String renderingPage = (String) modelAndView.getModel().get("renderingPage");
        assertEquals("/vxml/certificationCourse.vxml",renderingPage);
    }
}
