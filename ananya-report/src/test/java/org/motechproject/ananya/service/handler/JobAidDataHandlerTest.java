package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.JobAidContentMeasureService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidDataHandlerTest {


    private JobAidDataHandler handler;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private RegistrationLogService registrationLogService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new JobAidDataHandler(jobAidContentMeasureService,
                callDurationMeasureService,
                registrationMeasureService, registrationLogService);
    }

    @Test
    public void shouldHandleJobAidData() {
        String callId = "callId";
        String callerId = "callerId";
        LogData logData = new LogData(LogType.JOBAID, callId, callerId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        when(registrationLogService.getRegistrationLogFor(callerId)).thenReturn(new RegistrationLog(callerId, "", ""));

        handler.handleJobAidData(event);

        verify(jobAidContentMeasureService).createJobAidContentMeasure(callId);
    }
}
