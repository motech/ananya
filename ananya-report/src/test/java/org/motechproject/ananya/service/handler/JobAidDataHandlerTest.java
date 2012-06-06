package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.JobAidContentMeasureService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.model.MotechEvent;

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
        String callerId = "callerId";
        CallMessage logData = new CallMessage(CallMessageType.JOBAID, callId, callerId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleJobAidData(event);

        verify(registrationMeasureService).createRegistrationMeasureForCall(callerId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
        verify(jobAidContentMeasureService).createJobAidContentMeasure(callId);
    }
}
