package org.motechproject.ananya.support.diagnostics;

import org.motechproject.ananya.webservice.OnMobileSendSMSService;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class SMSDiagnostic {

    private OnMobileSendSMSService smsService;
    private String mobileNumber;
    private String senderId;
    private String smsMessage = "Ananya Test SMS from Production";

    @Autowired
    public SMSDiagnostic(OnMobileSendSMSService smsService,
                         @Value("#{ananyaProperties['sms.diagnostic.number']}") String mobileNumber,
                         @Value("#{ananyaProperties['sms.sender.id']}") String senderId) {
        this.smsService = smsService;
        this.mobileNumber = mobileNumber;
        this.senderId = senderId;
    }

    @Diagnostic(name = "sms")
    public DiagnosticsResult performDiagnosis() throws JMSException {
        boolean isSuccess = true;
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        try {
            String result = smsService.singlePush(mobileNumber, senderId, smsMessage);
            if ("failure".equals(result))
                throw new RuntimeException("SMS failed to deliver");
        } catch (Exception e) {
            diagnosticLog.add("Error in sending SMS");
            diagnosticLog.addError(e);
            isSuccess = false;
        }
        return new DiagnosticsResult(isSuccess, diagnosticLog.toString());
    }
}
