package org.motechproject.ananya.support.diagnostics;

import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;

public class AnanyaConfigurationDiagnosticTest {

    private Properties activemqProperties = new Properties();
    private Properties couchdbProperties = new Properties();
    private Properties ananyaProperties = new Properties();

    private AnanyaConfigurationDiagnostic diagnostic;

    @Before
    public void setUp() {
        diagnostic = new AnanyaConfigurationDiagnostic(ananyaProperties, couchdbProperties, activemqProperties);
    }

    @Test
    public void shouldPrintAllPropertiesToLog() throws JMSException {
        activemqProperties.put("A1", "A1Value");
        activemqProperties.put("A2", "A2Value");
        couchdbProperties.put("C1", "C1Value");
        ananyaProperties.put("AN1", "AN1Value");
        ananyaProperties.put("AN2", "AN2Value");

        String diagnosticLog = diagnostic.performDiagnosis().getMessage();

        assertTrue(diagnosticLog.contains("AN1=AN1Value"));
        assertTrue(diagnosticLog.contains("AN2=AN2Value"));
        assertTrue(diagnosticLog.contains("A1=A1Value"));
        assertTrue(diagnosticLog.contains("A2=A2Value"));
        assertTrue(diagnosticLog.contains("C1=C1Value"));
    }
}
