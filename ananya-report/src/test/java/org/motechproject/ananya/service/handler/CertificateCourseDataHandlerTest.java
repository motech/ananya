package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CertificateCourseDataHandlerTest {

    private CertificateCourseDataHandler handler;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new CertificateCourseDataHandler(courseItemMeasureService);
    }

    @Test
    public void shouldHandleCertificateCourseData() {
        LogData logData = new LogData(LogType.CALL_DURATION, "callId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCertificateCourseData(event);

        verify(courseItemMeasureService).createCourseItemMeasure("callId");
    }
}