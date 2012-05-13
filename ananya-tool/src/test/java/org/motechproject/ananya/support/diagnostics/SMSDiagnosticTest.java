package org.motechproject.ananya.support.diagnostics;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.webservice.OnMobileSendSMSService;

import javax.jms.JMSException;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

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

        DiagnosticLog diagnosticLog = smsDiagnostic.performDiagnosis();

        assertTrue(diagnosticLog.toString().contains("Error in sending SMS"));
        assertTrue(diagnosticLog.toString().contains("SMS failed to deliver"));
    }
}
