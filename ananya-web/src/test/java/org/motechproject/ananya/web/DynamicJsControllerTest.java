package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DynamicJsControllerTest {

    @Mock
    AllNodes allNodes;

    @Mock
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    Properties properties;

    @Before
    public void setUp(){
        initMocks(this);
        when(properties.getProperty("url.version")).thenReturn("anyVersion");
    }

    @Test
    public void shouldGetMetadataWhenURIDoesNotContainAirtel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/generated/js/metadata.js");

        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);
        ModelAndView modelAndView = controller.serveMetaData(request, new MockHttpServletResponse());

        assertEquals(modelAndView.getViewName(),"metadata");
    }

    @Test
    public void shouldGetAirtelMetadataWhenURIDoesContainsAirtel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET","http://localhost:9979/ananya/airtel/generated/js/metadata.js");

        DynamicJsController controller = new DynamicJsController(allNodes, frontLineWorkerService, properties);
        ModelAndView modelAndView = controller.serveMetaData(request, new MockHttpServletResponse());

        assertEquals(modelAndView.getViewName(),"metadata_airtel");
    }
}
