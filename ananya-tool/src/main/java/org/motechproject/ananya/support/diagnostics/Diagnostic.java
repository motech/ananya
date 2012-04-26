package org.motechproject.ananya.support.diagnostics;

import javax.jms.JMSException;

public interface Diagnostic {
    DiagnosticLog performDiagnosis() throws JMSException;
}
