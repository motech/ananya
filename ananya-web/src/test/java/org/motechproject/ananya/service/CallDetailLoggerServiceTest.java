package org.motechproject.ananya.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.repository.AllCallDetailLogs;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDetailLoggerServiceTest {
    private CallDetailLoggerService callDetailLoggerService;
    @Mock
    private AllCallDetailLogs allCallDetailLogs;

    @Before
    public void setUp() {
        initMocks(this);
        callDetailLoggerService = new CallDetailLoggerService(allCallDetailLogs);
    }

    @Test
    public void shouldSaveCallDetailLog() {
        CallDetailLog log = new CallDetailLog("caller", "callerId", CallEvent.REGISTRATION_START, DateTime.now(),"");

        callDetailLoggerService.save(log);

        verify(allCallDetailLogs).addIfAbsent(log);
    }

}
