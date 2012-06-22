package org.motechproject.ananya.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.service.CertificateCourseService;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


public class CertificateCourseCallDataControllerTest {

    private CertificateCourseCallDataController certificateCourseCallDataController;

    @Mock
    private CertificateCourseService certificateCourseService;

    @Before
    public void setup() {
        initMocks(this);
        certificateCourseCallDataController = new CertificateCourseCallDataController(certificateCourseService);
    }

    @Test
    public void shouldCallCourseServiceWithServiceRequestFromHttpPayLoad() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("callId")).thenReturn("123-456");
        when(request.getParameter("callerId")).thenReturn("123");
        when(request.getParameter("calledNumber")).thenReturn("57711");
        when(request.getParameter("dataToPost")).thenReturn("[]");


        ArgumentCaptor<CertificateCourseServiceRequest> captor = ArgumentCaptor.forClass(CertificateCourseServiceRequest.class);
        String response = certificateCourseCallDataController.handleDisconnect(request);

        verify(certificateCourseService).handleDisconnect(captor.capture());
        CertificateCourseServiceRequest certificateCourseServiceRequest = captor.getValue();

        assertThat(certificateCourseServiceRequest.getCallId(), is("123-456"));
        assertThat(certificateCourseServiceRequest.getCallerId(), is("123"));
        assertThat(certificateCourseServiceRequest.getCalledNumber(), is("57711"));
        Assert.assertThat(response, is(getReturnVxml()));
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
