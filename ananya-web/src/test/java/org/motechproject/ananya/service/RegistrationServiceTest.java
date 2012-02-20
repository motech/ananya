package org.motechproject.ananya.service;

import junit.framework.TestCase;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.request.RegistrationRequest;
import org.omg.CORBA.StringHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class RegistrationServiceTest  {

    private RegistrationService registrationService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;

    @Mock
    private ReportDataPublisher reportDataPublisher;

    @Mock
    private LogService logService;

    @Before
    public void setUp(){
        initMocks(this);
        registrationService = new RegistrationService(frontLineWorkerService, logService, reportDataPublisher);
    }

    @Test
    public void shouldRegisterNewFLW() {
        RegistrationRequest registrationRequest = new RegistrationRequest("123", "456", "ANM", "B001V005");
        final String id = "id";
        when(logService.registered(registrationRequest)).thenReturn(id);

        registrationService.register(registrationRequest);

        verify(logService).registered(registrationRequest);
        verify(reportDataPublisher).publishRegistration(argThat(new LogDataMatcher(new LogData(LogType.REGISTRATION,id))));
        verify(frontLineWorkerService).createNew("123",Designation.ANM,"B001V005");
    }

    @Test
    public void shouldSaveTranscribedName() throws WorkerDoesNotExistException {
        final String msisdn = "123";
        final String name = "myName";
        final String id = "id";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANM, "B001V005") { {setId(id);}};
        when(frontLineWorkerService.saveName(msisdn, name)).thenReturn(frontLineWorker);

        registrationService.saveTranscribedName(msisdn, name);

        verify(reportDataPublisher).publishRegistrationUpdate(argThat(new LogDataMatcher(new LogData(LogType.REGISTRATION_SAVE_NAME,id))));
    }

    public static class LogDataMatcher extends BaseMatcher<LogData> {
        private LogData logData;

        public LogDataMatcher(LogData logData){
            this.logData = logData;
        }

        @Override
        public boolean matches(Object o) {
            LogData actualLogData = (LogData) o;
            return this.logData.getDataId().equals(actualLogData.getDataId());
        }

        @Override
        public void describeTo(Description description) {
            throw new RuntimeException();
        }
    }
}
