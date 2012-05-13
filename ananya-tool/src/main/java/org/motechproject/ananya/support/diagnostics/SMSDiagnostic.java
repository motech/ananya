package org.motechproject.ananya.support.diagnostics;

import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.webservice.OnMobileSendSMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class SMSDiagnostic implements Diagnostic {

    private OnMobileSendSMSService smsService;

    @Autowired
    public SMSDiagnostic(OnMobileSendSMSService smsService) {
        this.smsService = smsService;
    }

    @Override
    public DiagnosticLog performDiagnosis() throws JMSException {
         DiagnosticLog diagnosticLog = new DiagnosticLog("SMS");



        return diagnosticLog;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
