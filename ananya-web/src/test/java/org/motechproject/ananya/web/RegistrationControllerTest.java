package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.repository.AllRecordings;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class RegistrationControllerTest {

    private RegistrationController controller;
    @Mock
    private FrontLineWorkerService flwService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AllRecordings allRecordings;
    @Mock
    private HttpSession session;


    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(flwService, allRecordings);
    }

    @Test
    public void shouldRegistrationVxmlWithLinkToJobAidIfEntryIsJobAidNumber() {
        when(request.getContextPath()).thenReturn("/ananya");

        ModelAndView modelAndView = controller.getCallFlow(request, "jobaid");

        assertEquals("register", modelAndView.getViewName());
        assertEquals("/ananya/vxml/jobaid.vxml", (String) modelAndView.getModel().get("nextFlow"));
        assertDesignations(modelAndView);
    }

    @Test
    public void shouldRegistrationVxmlWithLinkToCourseIfEntryIsCertificateCourseNumber() {
        when(request.getContextPath()).thenReturn("/ananya");

        ModelAndView modelAndView = controller.getCallFlow(request, "certificatecourse");

        assertEquals("register", modelAndView.getViewName());
        String nextFlow = (String) modelAndView.getModel().get("nextFlow");
        assertEquals("/ananya/vxml/certificatecourse.vxml", nextFlow);
        assertDesignations(modelAndView);
    }

    @Test
    public void shouldCaptureRecordWavFilesAndRegisterFLW() throws Exception {
        String msisdn = "123";
        String path = "/path";
        String village = "S01D001B001V004";

        ServletFileUpload upload = mock(ServletFileUpload.class);
        ServletContext context = mock(ServletContext.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getContentType()).thenReturn("multipart/");

        FileItem msisdnItem = mock(FileItem.class);
        FileItem designationItem = mock(FileItem.class);
        FileItem panchayatItem = mock(FileItem.class);
        List items = Arrays.asList(msisdnItem, designationItem, panchayatItem);

        when(context.getRealPath("/recordings/")).thenReturn(path);
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(context);
        when(upload.parseRequest(request)).thenReturn(items);

        SetUpExpectationsFor("session.connection.remote.uri", msisdn);
        SetUpExpectationsFor("designation", Designation.ASHA.name());
        SetUpExpectationsFor("panchayat", village);

        RegistrationController controllerSpy = spy(controller);
        doReturn(upload).when(controllerSpy).getUploader();

        ModelAndView modelAndView = controllerSpy.registerNew(request);

        verify(flwService).createNew(msisdn, Designation.ASHA, village);
        verify(allRecordings).store(msisdn, items, path);
        assertEquals("register-done", modelAndView.getViewName());

    }

    private void SetUpExpectationsFor(String fieldName, String itemName) {
        when(request.getParameter(fieldName)).thenReturn(itemName);
    }

    private void assertDesignations(ModelAndView modelAndView) {
        Map designations = (Map) modelAndView.getModel().get("designations");
        assertEquals(Designation.ANM.name(), designations.get(1));
        assertEquals(Designation.ASHA.name(), designations.get(2));
        assertEquals(Designation.ANGANWADI.name(), designations.get(3));
    }


}
        