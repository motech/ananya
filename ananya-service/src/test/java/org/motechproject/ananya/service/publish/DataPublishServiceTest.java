package org.motechproject.ananya.service.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataPublishServiceTest {

    @Mock
    private DbPublishService dbPublishService;
    @Mock
    private QueuePublishService queuePublishService;

    private DataPublishService dataPublishService;

    @Before
    public void setUp() {
        initMocks(this);
        dataPublishService = new DataPublishService(dbPublishService, queuePublishService, "queue");
    }

    @Test
    public void shouldPickTheRightImplementationBasedOnConfiguration() {
        String callerId = "321";
        String callId = "123";
        dataPublishService.publishCallDisconnectEvent(callId, callerId, ServiceType.JOB_AID);
        verify(dbPublishService, never()).publishCallDisconnectEvent(callId, callerId, ServiceType.JOB_AID);
        verify(queuePublishService).publishCallDisconnectEvent(callId, callerId, ServiceType.JOB_AID);
    }
}
