package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationLogServiceTest {

    @Mock
    private AllRegistrationLogs allRegistrationLogs;
    private RegistrationLogService registrationLogService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationLogService = new RegistrationLogService(allRegistrationLogs);
    }

    @Test
    public void shouldCallAllRegistrationLogsToPersist() {
        RegistrationLog registrationLog = new RegistrationLog();
        registrationLogService.addNew(registrationLog);
        verify(allRegistrationLogs).add(registrationLog);
    }
}
