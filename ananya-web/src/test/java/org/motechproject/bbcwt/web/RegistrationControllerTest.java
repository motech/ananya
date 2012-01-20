package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.bbcwt.repository.AllRecordings;
import org.springframework.ui.ModelMap;
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
    public void shouldCaptureRecordWavFilesAndRegisterFLW() throws Exception {
        String msisdn = "123";
        String path = "/path";
        ServletFileUpload upload = mock(ServletFileUpload.class);
        FileItem msisdnItem = mock(FileItem.class);
        FileItem fileItem = mock(FileItem.class);
        ServletContext context = mock(ServletContext.class);
        List items = Arrays.asList(msisdnItem, fileItem);

        when(context.getRealPath("/recordings/")).thenReturn(path);
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(context);
        when(upload.parseRequest(request)).thenReturn(items);
        when(msisdnItem.getFieldName()).thenReturn("msisdn");
        when(msisdnItem.isFormField()).thenReturn(true);
        when(msisdnItem.getString()).thenReturn(msisdn);

        RegistrationController controllerSpy = spy(controller);
        doReturn(upload).when(controllerSpy).getUploader();

        ModelAndView modelAndView = controllerSpy.registerNew(request);

        verify(flwService).createNew(msisdn);
        verify(allRecordings).store(msisdn,items,path);
        assertEquals("register-done-flw",modelAndView.getViewName());
    }


}
        