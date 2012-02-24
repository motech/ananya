package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationRequest;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.RegistrationService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class RegistrationControllerTest {

    private RegistrationController controller;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AllRecordings allRecordings;
    @Mock
    private HttpSession session;
    @Mock
    private RegistrationService registrationService;


    @Before
    public void setUp() {
        initMocks(this);
        controller = new RegistrationController(allRecordings, registrationService);
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

        verify(registrationService).register(argThat(new RegistrationRequestMatcher(new RegistrationRequest(callerNo, calledNo, Designation.ASHA.name(), panchayat,""))));

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
        FrontLineWorker mockFlw = new FrontLineWorker(msisdn, Designation.ANGANWADI, "D001S01","");
        mockFlw.setId(id);

        when(request.getParameter("msisdn")).thenReturn(msisdn);
        when(request.getParameter("name")).thenReturn(name);

        controller.saveTranscribedName(request);

        verify(registrationService).saveTranscribedName(msisdn, name);
    }

    public static class RegistrationRequestMatcher extends BaseMatcher<RegistrationRequest> {
        private RegistrationRequest request;

        public RegistrationRequestMatcher(RegistrationRequest request) {
            this.request = request;
        }

        @Override
        public void describeTo(Description description) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean matches(Object o) {
            RegistrationRequest actualRequest = (RegistrationRequest)o;
            return request.getCallerId().equals(actualRequest.getCallerId()) && request.getCalledNumber().equals(actualRequest.getCalledNumber()) ;
        }
    }

}
        