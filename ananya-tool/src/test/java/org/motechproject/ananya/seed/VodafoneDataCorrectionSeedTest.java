package org.motechproject.ananya.seed;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.support.synchroniser.service.SMSService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class VodafoneDataCorrectionSeedTest {
    @Mock
    private AllRegistrationLogs allRegistrationLogs;
    @Mock
    private AllSMSLogs allSMSLogs;
    @Mock
    private SMSService smsService;

    private VodafoneDataCorrectionSeed seed;

    @Before
    public void setUp() {
        initMocks(this);
        seed = new VodafoneDataCorrectionSeed(allRegistrationLogs, allSMSLogs, smsService);
    }

    @Test
    public void shouldPickRegistrationLogsWithoutCallIdAndFixThem() throws NoSuchFieldException {

        RegistrationLog log1 = new RegistrationLog("9986574410-123456789", "9986574410", "vodafone", "bihar");
        RegistrationLog log2 = new RegistrationLog("", "9986574411", "vodafone", "bihar");
        RegistrationLog log3 = new RegistrationLog("9986574412-123456789", "9986574412", "vodafone", "bihar");
        RegistrationLog log4 = new RegistrationLog(null, "9986574413", "vodafone", "bihar");

        List<RegistrationLog> registrationLogs = Arrays.asList(log1, log2, log3, log4);
        when(allRegistrationLogs.getAll()).thenReturn(registrationLogs);

        seed.correctRegistrationLogsWithMissingCallIds();

        verify(allRegistrationLogs, never()).update(log1);
        verify(allRegistrationLogs).update(log2);
        verify(allRegistrationLogs, never()).update(log3);
        verify(allRegistrationLogs).update(log4);

        assertThat(log1.getCallId(), is("9986574410-123456789"));
        assertThat(log2.getCallId(), is("9986574411-1341603000000"));
        assertThat(log3.getCallId(), is("9986574412-123456789"));
        assertThat(log4.getCallId(), is("9986574413-1341603000000"));
    }

    @Test
    public void shouldBuildAndSendSMS() {
        SMSLog smsLog1 = new SMSLog("callId1", "callerId1", "locationId1", 1);
        SMSLog smsLog2 = new SMSLog("callId2", "callerId2", "locationId2", 2);
        List<SMSLog> smsLogs = Arrays.asList(smsLog1, smsLog2);
        when(allSMSLogs.getAll()).thenReturn(smsLogs);

        seed.correctSMSLogs();

        verify(smsService).buildAndSendSMS("callerId1", "locationId1", 1);
        verify(smsService).buildAndSendSMS("callerId2", "locationId2", 2);
    }
}
