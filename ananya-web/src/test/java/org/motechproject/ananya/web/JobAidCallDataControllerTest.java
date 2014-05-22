package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.service.JobAidService;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidCallDataControllerTest {
    @Mock
    private JobAidService jobAidService;

    private JobAidCallDataController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new JobAidCallDataController(jobAidService);
    }

    @Test
    public void shouldCallJobAidServiceWithServiceRequestFromHttpPayLoad() {
        String response = controller.handleDisconnect("123-456", "123", "airtel", "bihar", "57711", "bhojpuri", "[]", "['prompt1', 'prompt2']", 111);

        ArgumentCaptor<JobAidServiceRequest> captor = ArgumentCaptor.forClass(JobAidServiceRequest.class);
        verify(jobAidService).handleDisconnect(captor.capture());
        JobAidServiceRequest jobAidServiceRequest = captor.getValue();

        assertThat(jobAidServiceRequest.getCallId(), is("123-456"));
        assertThat(jobAidServiceRequest.getCallerId(), is("123"));
        assertThat(jobAidServiceRequest.getCalledNumber(), is("57711"));
        assertThat(jobAidServiceRequest.getCallDuration(), is(111));
        assertThat(jobAidServiceRequest.getPrompts().size(), is(2));
        assertThat(response, is(getReturnVxml()));
    }


    private String getReturnVxml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<vxml version=\"2.1\" xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml21/vxml.xsd\">");
        builder.append("<form id=\"endCall\">");
        builder.append("<block><disconnect/></block>");
        builder.append("</form></vxml>");
        return builder.toString();
    }


}
