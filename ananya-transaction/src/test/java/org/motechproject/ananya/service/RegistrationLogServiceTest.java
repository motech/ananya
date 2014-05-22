package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationLogServiceTest {
    private RegistrationLogService registrationLogService;
    @Mock
    private AllRegistrationLogs allRegistrationLogs;

    @Before
    public void setUp() {
        initMocks(this);
        registrationLogService = new RegistrationLogService(allRegistrationLogs);
    }

    @Test
    public void shouldDeleteRegistrationLogsForCallId() {
        String callId = "callId";
        registrationLogService.deleteFor(callId);
        verify(allRegistrationLogs).remove(any(RegistrationLog.class));
    }
    
    @Test
    public void shouldAddRegistrationLog(){
        RegistrationLog registrationLog = new RegistrationLog("callId", "callerId", "", "");
        registrationLogService.add(registrationLog);
        verify(allRegistrationLogs).add(registrationLog);
    }
}
