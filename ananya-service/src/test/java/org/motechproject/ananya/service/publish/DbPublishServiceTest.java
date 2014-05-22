package org.motechproject.ananya.service.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.service.handler.CertificateCourseDataHandler;
import org.motechproject.ananya.service.handler.JobAidDataHandler;
import org.motechproject.event.MotechEvent;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DbPublishServiceTest {
    @Mock
    CertificateCourseDataHandler certificateCourseDataHandler;
    @Mock
    JobAidDataHandler jobAidDataHandler;

    private DbPublishService dbPublishService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPublishService = new DbPublishService(certificateCourseDataHandler, jobAidDataHandler);
    }

    @Test
    public void shouldPublishCallDisconnectEventForCertificateCourse() throws Exception {
        String callId = "141414";

        dbPublishService.publishDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(certificateCourseDataHandler).handleCertificateCourseData(captor.capture());
        MotechEvent motechEvent = captor.getValue();
        assertEquals(1, motechEvent.getParameters().values().size());
        CallMessage logData = (CallMessage) motechEvent.getParameters().get("logData");
        assertEquals(callId, logData.getCallId());
    }

    @Test
    public void shouldPublishCallDisconnectEventForJobAidCourse() throws Exception {
        String callId = "141414";

        dbPublishService.publishDisconnectEvent(callId, ServiceType.JOB_AID);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(jobAidDataHandler).handleJobAidData(captor.capture());
        MotechEvent motechEvent = captor.getValue();
        assertEquals(1, motechEvent.getParameters().values().size());
        CallMessage logData = (CallMessage) motechEvent.getParameters().get("logData");
        assertEquals(callId, logData.getCallId());
    }
}
