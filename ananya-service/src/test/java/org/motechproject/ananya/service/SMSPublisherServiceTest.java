package org.motechproject.ananya.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.publish.DataPublishService;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSPublisherServiceTest {
    SMSPublisherService smsPublisherService;
    @Mock
    private DataPublishService dataPublishService;

    @Before
    public void setUp() {
        initMocks(this);
        smsPublisherService = new SMSPublisherService(dataPublishService);
    }

    @Test
    public void shouldPublishSmsSentEvent(){
        String msisdn = "12345";

        smsPublisherService.publishSMSSent(msisdn);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(dataPublishService).publishSMSSent(captor.capture());
        LogData logData = captor.getValue();
        assertEquals(msisdn,logData.getDataId());
        assertEquals(LogType.SMS_SENT,logData.getType());
    }
}
