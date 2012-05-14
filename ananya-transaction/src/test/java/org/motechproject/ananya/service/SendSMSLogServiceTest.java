package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SendSMSLog;
import org.motechproject.ananya.repository.AllSendSMSLogs;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSLogServiceTest{
    private SendSMSLogService sendSMSLogService;

    @Mock
    private AllSendSMSLogs allSendSMSLogs;

    @Before
    public void setUp() {
        initMocks(this);
        sendSMSLogService = new SendSMSLogService(allSendSMSLogs);
    }

    @Test
    public void shouldAddNewSendSMSLog() {
        String callerId = "99887766";
        sendSMSLogService.add(new SendSMSLog(callerId));

        ArgumentCaptor<SendSMSLog> captor = ArgumentCaptor.forClass(SendSMSLog.class);
        verify(allSendSMSLogs).add(captor.capture());

        assertEquals(captor.getValue().getCallerId(), callerId);
    }
}
