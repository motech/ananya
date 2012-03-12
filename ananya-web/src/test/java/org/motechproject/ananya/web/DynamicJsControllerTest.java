package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.functional.MyWebClient;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DynamicJsControllerTest {

    private MyWebClient myWebClient;

    @Mock
    AllNodes allNodes;

    @Mock
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    Properties properties;

    @Before
    public void setUp(){
        initMocks(this);
        myWebClient = new MyWebClient();
        when(properties.getProperty("url.version")).thenReturn("v1");
    }

    @Test
    public void shouldGetMetadataWhenURIDoesNotContainAirtel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/v1/generated/js/metadata.js");
        request.setServletPath("/v1");

        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);
        ModelAndView modelAndView = controller.serveMetaData(request, new MockHttpServletResponse());

        assertEquals("metadata", modelAndView.getViewName());
    }

    @Test
    public void shouldGetAirtelMetadataWhenURIContainsAirtel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/airtel/v1/generated/js/metadata.js");
        request.setServletPath("/airtel/v1");

        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);
        ModelAndView modelAndView = controller.serveMetaData(request, new MockHttpServletResponse());

        assertEquals("metadataairtel", modelAndView.getViewName());
    }

    @Test
    public void shouldGetMetadataEvenWhenURIDoesNotHaveVersionAndVendor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/generated/js/metadata.js");
        request.setServletPath("/generated/js/metadata.js");

        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);
        ModelAndView modelAndView = controller.serveMetaData(request, new MockHttpServletResponse());

        assertEquals("metadata", modelAndView.getViewName());
    }

    @Test
    public void shouldReturnCallerDataForJobAidWithUsageValues() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/generated/js/dynamic/joabaid/caller_data.js");
        request.addParameter("callerId", "12345");
        request.addParameter("operator", "airtel");
        request.setServletPath("/dynamic/jobaid/caller_data.js");
        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);

        when(frontLineWorkerService.createJobAidCallerData("12345", "airtel")).thenReturn(
                new JobAidCallerDataResponse(true,true, new HashMap<String, Integer>()));
        ModelAndView callerDataForJobAid = controller.getCallerDataForJobAid(request, new MockHttpServletResponse());

        assertEquals("job_aid_caller_data",callerDataForJobAid.getViewName() );
        assertTrue((Boolean) callerDataForJobAid.getModel().get("isCallerRegistered"));
        assertTrue((Boolean) callerDataForJobAid.getModel().get("hasReachedMaxUsageForMonth"));
        assertNotNull((HashMap<String, Integer>) callerDataForJobAid.getModel().get("promptsHeard"));
    }
}
