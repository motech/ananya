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
import org.springframework.mock.web.MockHttpServletRequest;
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
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://localhost:9979/ananya/generated/js/dynamic/joabaid/caller_data.js");
        String callerId = "919986574410";
        String callId = "919986574410-1019877";
        request.addParameter("callerId", callerId);
        request.addParameter("callId", callId);
        request.addParameter("operator", "airtel");
        request.addParameter("circle", "circle");
        request.setServletPath("/dynamic/jobaid/caller_data.js");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setRegistrationStatus(RegistrationStatus.REGISTERED);
        frontLineWorker.setCurrentJobAidUsage(1000);
        frontLineWorker.markPromptHeard("some.wav");

        JobAidCallerDataResponse jobAidCallerDataResponse = new JobAidCallerDataResponse(frontLineWorker, 2000);
        when(jobAidService.createCallerData(callId, callerId, "airtel", "circle")).thenReturn(jobAidCallerDataResponse);

        ModelAndView callerDataForJobAid = controller.getCallerDataForJobAid(request, new MockHttpServletResponse());

        assertEquals("job_aid_caller_data", callerDataForJobAid.getViewName());
        assertTrue((Boolean) callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(1000, callerDataForJobAid.getModel().get("currentJobAidUsage"));
        assertEquals(2000, callerDataForJobAid.getModel().get("maxAllowedUsageForOperator"));
        assertNotNull(callerDataForJobAid.getModel().get("promptsHeard"));
    }

    @Test
    public void shouldReturnCallerDataForCertificateCourseWithBookMarkAndScores() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://localhost:9979/ananya/generated/js/dynamic/caller_data.js");
        boolean isUserRegistered = true;
        String current_bookmark = "current_bookmark";
        String callerId = "919986574410";
        String callId = "919986574410-12345";
        String operator = "airtel";
        String circle = "circle";
        HashMap<String, Integer> scoresByChapter = new HashMap<String, Integer>();
        request.addParameter("callId", callId);
        request.addParameter("callerId", callerId);
        request.addParameter("operator", operator);
        request.addParameter("circle", circle);
        request.setServletPath("/dynamic/caller_data.js");
        when(certificateCourseService.createCallerData(callId, callerId, operator, circle)).thenReturn(
                new CertificateCourseCallerDataResponse(current_bookmark, isUserRegistered, scoresByChapter));

        ModelAndView callerDataForJobAid = controller.getCallerData(request, new MockHttpServletResponse());

        assertEquals("caller_data", callerDataForJobAid.getViewName());
        assertEquals(current_bookmark, callerDataForJobAid.getModel().get("bookmark"));
        assertEquals(isUserRegistered, callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(scoresByChapter, callerDataForJobAid.getModel().get("scoresByChapter"));
    }


}
