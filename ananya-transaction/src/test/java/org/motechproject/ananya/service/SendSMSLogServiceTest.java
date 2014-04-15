package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSLogServiceTest{
    private SMSLogService sendSMSLogService;

    @Mock
    private AllSMSLogs allSendSMSLogs;

    @Before
    public void setUp() {
        initMocks(this);
        sendSMSLogService = new SMSLogService(allSendSMSLogs);
    }

    @Test
    public void shouldAddNewSendSMSLog() {
        String callerId = "99887766";
        sendSMSLogService.add(new SMSLog(callerId));

        ArgumentCaptor<SMSLog> captor = ArgumentCaptor.forClass(SMSLog.class);
        verify(allSendSMSLogs).add(captor.capture());

        assertEquals(captor.getValue().getCallerId(), callerId);
    }
}
