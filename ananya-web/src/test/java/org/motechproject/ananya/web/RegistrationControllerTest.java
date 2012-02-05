package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

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
    public void shouldRegisterFLWWithLocation() throws Exception {
        String msisdn = "123";
        String village = "S01D001B001V004";

        when(request.getParameter("session.connection.remote.uri")).thenReturn(msisdn);
        when(request.getParameter("designation")).thenReturn(Designation.ASHA.name());
        when(request.getParameter("panchayat")).thenReturn(village);

        ModelAndView modelAndView = controller.registerNew(request);

        verify(flwService).createNew(msisdn, Designation.ASHA, village);
        assertEquals("register-done", modelAndView.getViewName());

    }

    @Test
    public void shouldRecordFLWName() throws Exception {
        String msisdn = "123";
        String path = "/path";
        ServletFileUpload upload = mock(ServletFileUpload.class);
        ServletContext context = mock(ServletContext.class);
        FileItem msisdnItem = mock(FileItem.class);
        List items = Arrays.asList(msisdnItem);

        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(context);
        when(context.getRealPath("/recordings/")).thenReturn(path);
        when(upload.parseRequest(request)).thenReturn(items);

        when(msisdnItem.isFormField()).thenReturn(true);
        when(msisdnItem.getFieldName()).thenReturn("session.connection.remote.uri");
        when(msisdnItem.getString()).thenReturn(msisdn);

        RegistrationController controllerSpy = spy(controller);
        doReturn(upload).when(controllerSpy).getUploader();

        ModelAndView modelAndView = controllerSpy.recordName(request);

        verify(allRecordings).store(msisdn, items, path);
        assertEquals("register-done", modelAndView.getViewName());

    }

}
        