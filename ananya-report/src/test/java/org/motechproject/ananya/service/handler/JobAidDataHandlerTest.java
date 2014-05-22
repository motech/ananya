package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidDataHandlerTest {


    private JobAidDataHandler handler;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new JobAidDataHandler(jobAidContentMeasureService,
                callDurationMeasureService,
                registrationMeasureService);
    }

    @Test
    public void shouldHandleJobAidData() {
        String callId = "callId";
        CallMessage logData = new CallMessage(CallMessageType.JOBAID, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", new Integer(3)); //some other parameter
        map.put("2", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleJobAidData(event);

        verify(registrationMeasureService).createFor(callId);
        verify(callDurationMeasureService).createFor(callId);
        verify(jobAidContentMeasureService).createFor(callId);
    }
}
