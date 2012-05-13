package org.motechproject.ananya.support.diagnostics;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;

import javax.jms.JMSException;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;

public class ConfigurationDiagnosticTest {

    private Properties activemqProperties = new Properties();
    private Properties couchdbProperties = new Properties();
    private Properties ananyaProperties = new Properties();

    private ConfigurationDiagnostic diagnostic;

    @Before
    public void setUp() {
        diagnostic = new ConfigurationDiagnostic(ananyaProperties, couchdbProperties, activemqProperties);
    }

    @Test
    public void shouldPrintAllPropertiesToLog() throws JMSException {
        activemqProperties.put("A1", "A1Value");
        activemqProperties.put("A2", "A2Value");
        couchdbProperties.put("C1", "C1Value");
        ananyaProperties.put("AN1", "AN1Value");
        ananyaProperties.put("AN2", "AN2Value");

        DiagnosticLog diagnosticLog = diagnostic.performDiagnosis();

        assertTrue(diagnosticLog.toString().contains("AN1=AN1Value"));
        assertTrue(diagnosticLog.toString().contains("AN2=AN2Value"));
        assertTrue(diagnosticLog.toString().contains("A1=A1Value"));
        assertTrue(diagnosticLog.toString().contains("A2=A2Value"));
        assertTrue(diagnosticLog.toString().contains("C1=C1Value"));
    }
}
