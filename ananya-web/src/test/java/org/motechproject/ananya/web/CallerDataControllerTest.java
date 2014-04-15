package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
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
        controller = new CallerDataController(jobAidService, certificateCourseService);
    }

    @Test
    public void shouldReturnCallerDataForJobAidWithUsageValues() throws Exception {
        String callerId = "919986574410";
        String callId = "919986574410-1019877";
        String circle = "circle";
        String operator = "airtel";

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setRegistrationStatus(RegistrationStatus.REGISTERED);
        frontLineWorker.setCurrentJobAidUsage(1000);
        frontLineWorker.markPromptHeard("some.wav");

        MockHttpServletResponse response = new MockHttpServletResponse();
        JobAidCallerDataResponse jobAidCallerDataResponse = new JobAidCallerDataResponse(frontLineWorker, 2000);

        when(jobAidService.getCallerData(any(JobAidServiceRequest.class))).thenReturn(jobAidCallerDataResponse);

        ModelAndView callerDataForJobAid = controller.getCallerDataForJobAid(response, callId, callerId, operator, circle);

        ArgumentCaptor<JobAidServiceRequest> captor = ArgumentCaptor.forClass(JobAidServiceRequest.class);
        verify(jobAidService).getCallerData(captor.capture());
        JobAidServiceRequest captured = captor.getValue();
        assertEquals(callId, captured.getCallId());
        assertEquals(callerId, captured.getCallerId());
        assertEquals(operator, captured.getOperator());
        assertEquals(circle, captured.getCircle());

        assertEquals("job_aid_caller_data", callerDataForJobAid.getViewName());
        assertTrue((Boolean) callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(1000, callerDataForJobAid.getModel().get("currentJobAidUsage"));
        assertEquals(2000, callerDataForJobAid.getModel().get("maxAllowedUsageForOperator"));
        assertNotNull(callerDataForJobAid.getModel().get("promptsHeard"));
    }

    @Test
    public void shouldReturnCallerDataForCertificateCourseWithBookMarkAndScores() throws Exception {
        String current_bookmark = "current_bookmark";
        String callerId = "919986574410";
        String callId = "919986574410-12345";
        String operator = "airtel";
        String circle = "circle";
        String langauge = "langauge";
        boolean isUserRegistered = true;
        HashMap<String, Integer> scoresByChapter = new HashMap<String, Integer>();

        MockHttpServletResponse response = new MockHttpServletResponse();
        CertificateCourseCallerDataResponse certificateCourseCallerDataResponse = new CertificateCourseCallerDataResponse(current_bookmark, isUserRegistered, langauge, scoresByChapter);

        when(certificateCourseService.createCallerData(any(CertificateCourseServiceRequest.class))).thenReturn(certificateCourseCallerDataResponse);

        ModelAndView callerDataForCourse = controller.getCallerDataForCourse(response, callId, callerId, operator, circle);

        ArgumentCaptor<CertificateCourseServiceRequest> captor = ArgumentCaptor.forClass(CertificateCourseServiceRequest.class);
        verify(certificateCourseService).createCallerData(captor.capture());
        CertificateCourseServiceRequest captured = captor.getValue();
        assertEquals(callId, captured.getCallId());
        assertEquals(callerId, captured.getCallerId());
        assertEquals(operator, captured.getOperator());
        assertEquals(circle, captured.getCircle());

        assertEquals("caller_data", callerDataForCourse.getViewName());
        assertEquals(current_bookmark, callerDataForCourse.getModel().get("bookmark"));
        assertEquals(isUserRegistered, callerDataForCourse.getModel().get("isCallerRegistered"));
        assertEquals(scoresByChapter, callerDataForCourse.getModel().get("scoresByChapter"));
    }


}
