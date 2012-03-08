package org.motechproject.ananya.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.request.RegistrationRequest;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.request.LogRegistrationRequest;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class RegistrationServiceTest  {

    private RegistrationService registrationService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;

    @Mock
    private ReportPublisherService reportDataPublisher;

    @Mock
    private RegistrationLogService logService;

    @Before
    public void setUp(){
        initMocks(this);
        registrationService = new RegistrationService(frontLineWorkerService, logService, reportDataPublisher);
    }

    @Test
    public void shouldRegisterNewFLW() {
        LogRegistrationRequest logRegistrationRequest = new LogRegistrationRequest("123", "456", "ANM", "B001V005", "");
        RegistrationRequest registrationRequest = new RegistrationRequest("123", "456", "ANM", "B001V005","");
        final String id = "id";
        when(logService.registered(any(LogRegistrationRequest.class))).thenReturn(id);

        registrationService.register(registrationRequest);

        verify(logService).registered(argThat(new LogRegistrationRequestMatcher(logRegistrationRequest)));
        verify(reportDataPublisher).publishRegistration(argThat(new LogDataMatcher(new LogData(LogType.REGISTRATION,id))));
        verify(frontLineWorkerService).createNew("123",Designation.ANM,"B001V005", registrationRequest.getOperator());
    }

    @Test
    public void shouldSaveTranscribedName() throws WorkerDoesNotExistException {
        final String msisdn = "123";
        final String name = "myName";
        final String id = "id";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANM, "B001V005","") { {setId(id);}};
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
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class LogRegistrationRequestMatcher extends BaseMatcher<LogRegistrationRequest> {
        private LogRegistrationRequest request;

        public LogRegistrationRequestMatcher(LogRegistrationRequest request) {
            this.request = request;
        }

        @Override
        public void describeTo(Description description) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean matches(Object o) {
            LogRegistrationRequest actualRequest = (LogRegistrationRequest)o;
            return request.callerId().equals(actualRequest.callerId()) && request.calledNumber().equals(actualRequest.calledNumber()) ;
        }
    }
}
