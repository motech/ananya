package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallerDataControllerTest {

    @Mock
    private CertificateCourseService certificateCourseService;
    @Mock
    private JobAidService jobAidService;

    private CallerDataController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CallerDataController(jobAidService, certificateCourseService);;
    }

    @Test
    public void shouldReturnCallerDataForJobAidWithUsageValues() throws Exception {
        String callerId = "919986574410";
        String callId = "919986574410-1019877";

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setRegistrationStatus(RegistrationStatus.REGISTERED);
        frontLineWorker.setCurrentJobAidUsage(1000);
        frontLineWorker.markPromptHeard("some.wav");

        JobAidCallerDataResponse jobAidCallerDataResponse = new JobAidCallerDataResponse(frontLineWorker, 2000);
        when(jobAidService.createCallerData(callId, callerId, "airtel", "circle")).thenReturn(jobAidCallerDataResponse);

        ModelAndView callerDataForJobAid = controller.getCallerDataForJobAid(new MockHttpServletResponse(), callId, callerId, "airtel", "circle");

        assertEquals("job_aid_caller_data", callerDataForJobAid.getViewName());
        assertTrue((Boolean) callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(1000, callerDataForJobAid.getModel().get("currentJobAidUsage"));
        assertEquals(2000, callerDataForJobAid.getModel().get("maxAllowedUsageForOperator"));
        assertNotNull(callerDataForJobAid.getModel().get("promptsHeard"));
    }

    @Test
    public void shouldReturnCallerDataForCertificateCourseWithBookMarkAndScores() throws Exception {
        boolean isUserRegistered = true;
        String current_bookmark = "current_bookmark";
        String callerId = "919986574410";
        String callId = "919986574410-12345";
        String operator = "airtel";
        String circle = "circle";
        HashMap<String, Integer> scoresByChapter = new HashMap<String, Integer>();
       
        when(certificateCourseService.createCallerData(callId, callerId, operator, circle)).thenReturn(
                new CertificateCourseCallerDataResponse(current_bookmark, isUserRegistered, scoresByChapter));

        ModelAndView callerDataForJobAid = controller.getCallerDataForCourse(new MockHttpServletResponse(), callId, callerId, operator, circle);

        assertEquals("caller_data", callerDataForJobAid.getViewName());
        assertEquals(current_bookmark, callerDataForJobAid.getModel().get("bookmark"));
        assertEquals(isUserRegistered, callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(scoresByChapter, callerDataForJobAid.getModel().get("scoresByChapter"));
    }


}
