package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSSentDataHandlerTest {

    private SMSSentDataHandler smsSentDataHandler;

    @Mock
    private SMSSentMeasureService smsSentMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        smsSentDataHandler = new SMSSentDataHandler(smsSentMeasureService);
    }

    @Test
    public void shouldHandleSMSSent() {
        String callerId = "9876543210";
        Map<String, Object> handlerParams = new HashMap<String, Object>();
        LogData logData = new LogData(LogType.SMS_SENT, callerId);
        handlerParams.put("0", logData);
        
        MotechEvent motechEvent = new MotechEvent(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY, handlerParams);
        smsSentDataHandler.handleSMSSent(motechEvent);

        verify(smsSentMeasureService).createSMSSentMeasure(eq(callerId));
    }
}
