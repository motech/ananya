package org.motechproject.ananya.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.service.ReportPublishService;
import org.motechproject.ananya.service.SMSPublisherService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSSentHandlerTest {

    @Mock
    private ReportPublishService reportPublisherService;
    private SMSSentHandler smsSentHandler;

    @Before
    public void setUp(){
        initMocks(this);
        smsSentHandler = new SMSSentHandler(reportPublisherService);
    }

    @Test
    public void shouldSendSingleSMS(){

        Map<String, Object> eventParams = new HashMap<String, Object>();
        Map<String, String> myParams = new HashMap<String, String>();
        String msisdn = "9876543210";
        myParams.put(SMSPublisherService.PARAMETER_MSISDN, msisdn);
        eventParams.put("0", myParams);

        MotechEvent motechEvent = new MotechEvent(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS, eventParams);
        smsSentHandler.publishSMSSent(motechEvent);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(reportPublisherService).publishSMSSent(captor.capture());

        LogData logData = captor.getValue();

        assertEquals(msisdn, logData.getDataId());
    }
}
