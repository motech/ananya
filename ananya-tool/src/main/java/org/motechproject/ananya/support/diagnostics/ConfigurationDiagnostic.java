package org.motechproject.ananya.support.diagnostics;

import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@Service
public class ConfigurationDiagnostic implements Diagnostic {

    @Override
    public DiagnosticLog performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog("CONFIGURATION");
        return diagnosticLog;
    }
}
