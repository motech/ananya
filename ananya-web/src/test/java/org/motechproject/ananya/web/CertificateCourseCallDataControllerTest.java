package org.motechproject.ananya.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.service.CertificateCourseService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


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
        ArgumentCaptor<CertificateCourseServiceRequest> captor = ArgumentCaptor.forClass(CertificateCourseServiceRequest.class);
        String response = certificateCourseCallDataController.handleDisconnect("123-456", "123","airtel","bihar", "57711", "[]");

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
