package org.motechproject.ananya.support.synchroniser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.seed.service.SMSSeedService;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class SMSSynchroniserTest {

    private SMSSynchroniser smsSynchroniser;
    @Mock
    private AllSMSLogs allSMSLogs;
    @Mock
    private SMSSeedService smsSeedService;

    @Before
    public void setUp() {
        initMocks(this);
        smsSynchroniser = new SMSSynchroniser(allSMSLogs, smsSeedService);
    }

    @Test
    public void shouldBuildAndSendSMS() {
        SMSLog smsLog1 = new SMSLog("callId1", "callerId1", "locationId1", 1);
        SMSLog smsLog2 = new SMSLog("callId2", "callerId2", "locationId2", 2);
        List<SMSLog> smsLogs = Arrays.asList(smsLog1, smsLog2);
        when(allSMSLogs.getAll()).thenReturn(smsLogs);

        SynchroniserLog synchroniserLog = smsSynchroniser.replicate();

        verify(smsSeedService).buildAndSendSMS("callerId1", "locationId1", 1);
        verify(smsSeedService).buildAndSendSMS("callerId2", "locationId2", 2);

        assertEquals(2, synchroniserLog.getItems().size());
        assertEquals("callerId1: Success", synchroniserLog.getItems().get(0).print());
        assertEquals("callerId2: Success", synchroniserLog.getItems().get(1).print());

    }

}
