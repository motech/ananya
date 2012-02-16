package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.ReportDataPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
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
    @Mock
    private RegistrationLogService logService;
    @Mock
    private ReportDataPublisher reportPublisher;


    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(flwService, allRecordings, logService, reportPublisher);
    }
    
    @Test
    public void shouldRegisterFLWWithLocation() throws Exception {
        String callerNo = "123";
        String calledNo = "456";
        String panchayat = "S01D001B001V004";

        when(request.getParameter("session.connection.remote.uri")).thenReturn(callerNo);
        when(request.getParameter("session.connection.local.uri")).thenReturn(calledNo);
        when(request.getParameter("designation")).thenReturn(Designation.ASHA.name());
        when(request.getParameter("panchayat")).thenReturn(panchayat);

        ModelAndView modelAndView = controller.registerNew(request);

        ArgumentCaptor<RegistrationLog> logCaptor = ArgumentCaptor.forClass(RegistrationLog.class);
        ArgumentCaptor<LogData> reportCaptor = ArgumentCaptor.forClass(LogData.class);

        verify(flwService).createNew(callerNo, Designation.ASHA, panchayat);
        verify(logService).addNew(logCaptor.capture());
        verify(reportPublisher).publishRegistration(reportCaptor.capture());

        RegistrationLog capturedLog = logCaptor.getValue();
        LogData capturedReport = reportCaptor.getValue();

        assertEquals(callerNo, getFieldValue(capturedLog,"callerId"));
        assertEquals(calledNo, getFieldValue(capturedLog,"calledNumber"));
        assertEquals(panchayat, getFieldValue(capturedLog,"panchayat"));
        assertEquals(LogType.REGISTRATION.name(), getFieldValue(capturedReport,"type"));
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

    @Test
    public void shouldSaveTranscribedName() throws Exception {
        String msisdn = "12345";
        String name = "flw_name";
        String id = "111";
        FrontLineWorker mockFlw = new FrontLineWorker(msisdn, Designation.ANGANWADI, "D001S01");
        mockFlw.setId(id);

        when(request.getParameter("msisdn")).thenReturn(msisdn);
        when(request.getParameter("name")).thenReturn(name);
        when(flwService.saveName(msisdn,name)).thenReturn(mockFlw);

        controller.saveTranscribedName(request);
        verify(flwService).saveName(msisdn, name);

        ArgumentCaptor<LogData> logDataCaptor = ArgumentCaptor.forClass(LogData.class);
        verify(reportPublisher).publishRegistrationUpdate(logDataCaptor.capture());

        LogData capturedReport = logDataCaptor.getValue();

        assertEquals(capturedReport.getDataId(), id);
    }

    private String getFieldValue(Object o, String field) {
        return ReflectionTestUtils.getField(o, field).toString();
    }

}
        