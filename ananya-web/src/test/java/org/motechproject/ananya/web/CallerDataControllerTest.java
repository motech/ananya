package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.JobAidService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallerDataControllerTest {

    @Mock
    AllNodes allNodes;

    @Mock
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    private CertificateCourseService certificateCourseService;

    @Mock
    private JobAidService jobAidService;

    @Mock
    Properties properties;
    private CallerDataController controller;

    @Before
    public void setUp() {
        initMocks(this);
        when(properties.getProperty("url.version")).thenReturn("v1");
        controller = new CallerDataController(jobAidService, certificateCourseService, properties);
    }

    @Test
    public void shouldReturnCallerDataForJobAidWithUsageValues() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://localhost:9979/ananya/generated/js/dynamic/joabaid/caller_data.js");
        request.addParameter("callerId", "12345");
        request.addParameter("operator", "airtel");
        request.setServletPath("/dynamic/jobaid/caller_data.js");

        when(jobAidService.createCallerData("12345", "airtel")).thenReturn(
                new JobAidCallerDataResponse(true, 1000, 2000, new HashMap<String, Integer>()));
        ModelAndView callerDataForJobAid = controller.getCallerDataForJobAid(request, new MockHttpServletResponse());

        assertEquals("job_aid_caller_data", callerDataForJobAid.getViewName());
        assertTrue((Boolean) callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(1000, callerDataForJobAid.getModel().get("currentJobAidUsage"));
        assertEquals(2000, callerDataForJobAid.getModel().get("maxAllowedUsageForOperator"));
        assertNotNull(callerDataForJobAid.getModel().get("promptsHeard"));
    }

    @Test
    public void shouldReturnCallerDataForCertificateCourseWithUsageValues() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://localhost:9979/ananya/generated/js/dynamic/caller_data.js");
        String current_bookmark = "current_bookmark";
        boolean isUserRegistered = true;
        String callerId = "12345";
        String operator = "airtel";
        HashMap<String, Integer> scoresByChapter = new HashMap<String, Integer>();
        request.addParameter("callerId", callerId);
        request.addParameter("operator", operator);
        request.setServletPath("/dynamic/caller_data.js");
        when(certificateCourseService.createCallerData(callerId, operator)).thenReturn(
                new CertificateCourseCallerDataResponse(current_bookmark, isUserRegistered, scoresByChapter));

        ModelAndView callerDataForJobAid = controller.getCallerData(request, new MockHttpServletResponse());

        assertEquals("caller_data", callerDataForJobAid.getViewName());
        assertEquals(current_bookmark, callerDataForJobAid.getModel().get("bookmark"));
        assertEquals(isUserRegistered, callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertEquals(scoresByChapter, callerDataForJobAid.getModel().get("scoresByChapter"));
    }


}
