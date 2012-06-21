package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.request.JobAidServiceRequest;
import org.motechproject.ananya.service.JobAidService;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class JobAidCallStateControllerTest {
    @Mock
    private JobAidService jobAidService;

    private JobAidCallStateController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new JobAidCallStateController(jobAidService);
    }

    @Test
    public void shouldCallJobAidServiceWithServiceRequestFromHttpPayLoad() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("callId")).thenReturn("123-456");
        when(request.getParameter("callerId")).thenReturn("123");
        when(request.getParameter("calledNumber")).thenReturn("57711");
        when(request.getParameter("callDuration")).thenReturn("111");
        when(request.getParameter("promptList")).thenReturn("['prompt1', 'prompt2']");
        when(request.getParameter("dataToPost")).thenReturn("[]");

        controller.handleDisconnect(request);

        ArgumentCaptor<JobAidServiceRequest> captor = ArgumentCaptor.forClass(JobAidServiceRequest.class);
        verify(jobAidService).handleDisconnect(captor.capture());
        JobAidServiceRequest jobAidServiceRequest = captor.getValue();

        assertThat(jobAidServiceRequest.getCallId(), is("123-456"));
        assertThat(jobAidServiceRequest.getCallerId(), is("123"));
        assertThat(jobAidServiceRequest.getCalledNumber(), is("57711"));
        assertThat(jobAidServiceRequest.getCallDuration(), is(111));
        assertThat(jobAidServiceRequest.getPrompts().size(), is(2));
    }
}
