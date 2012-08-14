package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BaseLog;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.seed.service.SMSSeedService;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSSynchroniserTest {

    private SMSSynchroniser smsSynchroniser;
    @Mock
    private AllSMSLogs allSMSLogs;
    @Mock
    private SMSSeedService smsSeedService;
    @Mock
    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);
        smsSynchroniser = new SMSSynchroniser(allSMSLogs, smsSeedService, properties);
    }

    @Test
    public void shouldBuildAndSendSMS() {
        long delta = DateTime.now().minusDays(4).getMillis();
        SMSLog smsLog1 = new SMSLog("callId1-" + delta, "callerId1", "locationId1", 1);
        SMSLog smsLog2 = new SMSLog("callId2-" + delta, "callerId2", "locationId2", 2);
        List<SMSLog> smsLogs = Arrays.asList(smsLog1, smsLog2);
        when(allSMSLogs.getAll()).thenReturn(smsLogs);
        when(properties.getProperty("synchroniser.log.delta.days")).thenReturn("2");

        SynchroniserLog synchroniserLog = smsSynchroniser.replicate();

        verify(smsSeedService).buildAndSendSMS("callerId1", "locationId1", 1);
        verify(smsSeedService).buildAndSendSMS("callerId2", "locationId2", 2);
        verify(allSMSLogs).remove(smsLog1);
        verify(allSMSLogs).remove(smsLog2);

        assertEquals(2, synchroniserLog.getItems().size());
        assertEquals("callerId1: Success", synchroniserLog.getItems().get(0).print());
        assertEquals("callerId2: Success", synchroniserLog.getItems().get(1).print());

    }

    @Test
    public void shouldFigureOutWhetherToProcessLog() throws Exception {
        BaseLog shouldProcessLog = new BaseLog("asd-" + DateTime.now().minusDays(3).getMillis(), "");
        BaseLog shouldNotProcessLog = new BaseLog("asd-" + DateTime.now().minusDays(2).getMillis(), "");
        when(properties.getProperty("synchroniser.log.delta.days")).thenReturn("2");

        assertTrue(smsSynchroniser.shouldProcessLog(shouldProcessLog));
        assertFalse(smsSynchroniser.shouldProcessLog(shouldNotProcessLog));
    }
}
