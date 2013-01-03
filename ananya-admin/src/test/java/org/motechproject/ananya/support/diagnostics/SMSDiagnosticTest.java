package org.motechproject.ananya.support.diagnostics;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.webservice.OnMobileSendSMSService;

import javax.jms.JMSException;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSDiagnosticTest {

    @Mock
    private OnMobileSendSMSService smsService;

    private String mobileNumber = "9987787955";
    private String senderId = "BI-55577";

    private SMSDiagnostic smsDiagnostic;

    @Before
    public void setUp() {
        initMocks(this);
        smsDiagnostic = new SMSDiagnostic(smsService, mobileNumber, senderId);
    }

    @Test
    public void shouldUseSMSWebServiceToSendSMSAndLogIfErrorOccurs() throws JMSException {
        String message = "Ananya Test SMS from Production";
        when(smsService.singlePush(mobileNumber, senderId, message)).thenReturn("failure");

        String diagnosticLog = smsDiagnostic.performDiagnosis().getMessage();

        assertTrue(diagnosticLog.contains("Error in sending SMS"));
        assertTrue(diagnosticLog.contains("SMS failed to deliver"));
    }
}
