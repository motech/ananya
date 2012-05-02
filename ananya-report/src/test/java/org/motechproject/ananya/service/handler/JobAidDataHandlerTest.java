package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.JobAidContentMeasureService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidDataHandlerTest {


    private JobAidDataHandler handler;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new JobAidDataHandler(jobAidContentMeasureService);
    }

    @Test
    public void shouldHandleJobAidData() {
        LogData logData = new LogData(LogType.JOBAID, "callId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleJobAidData(event);

        verify(jobAidContentMeasureService).createJobAidContentMeasure("callId");
    }
}
