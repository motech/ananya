package org.motechproject.ananya.support.diagnostics.base;

import javax.jms.JMSException;

public interface Diagnostic {
    DiagnosticLog performDiagnosis() throws JMSException;
}
