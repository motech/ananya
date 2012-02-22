package org.motechproject.ananya.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.request.LogRegistrationRequest;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class LogServiceTest {

    @Mock
    private AllRegistrationLogs allRegistrationLogs;
    private LogService logService;

    @Before
    public void setUp() {
        initMocks(this);
        logService = new LogService(allRegistrationLogs);
    }

    @Test
    public void shouldCallAllRegistrationLogsToPersist() {
        LogRegistrationRequest registrationRequest = new LogRegistrationRequest("123", "456", "ANM", "B001V004", "");

        logService.registered(registrationRequest);

        RegistrationLog registrationLog = new RegistrationLog("123", "456", DateTime.now(), DateTime.now(), "");
        RegistrationLogMatcher registrationLogMatcher = new RegistrationLogMatcher(registrationLog);
        verify(allRegistrationLogs).add(argThat(registrationLogMatcher));

    }

    public static class RegistrationLogMatcher extends BaseMatcher<RegistrationLog> {
        private RegistrationLog log;

        public RegistrationLogMatcher(RegistrationLog log) {
            this.log = log;
        }

        @Override
        public void describeTo(Description description) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean matches(Object o) {
            RegistrationLog actualLog = (RegistrationLog)o;
            return log.getCallerId().equals(actualLog.getCallerId()) && log.getCalledNumber().equals(actualLog.getCalledNumber()) ;
        }
    }
}
