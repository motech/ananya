package org.motechproject.ananya.support.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.Properties;
import java.util.TreeSet;

@Component
public class AnanyaConfigurationDiagnostic {

    private Properties ananyaProperties;
    private Properties couchdbProperties;
    private Properties activemqProperties;

    @Autowired
    public AnanyaConfigurationDiagnostic(@Qualifier("ananyaProperties") Properties ananyaProperties,
                                         @Qualifier("couchdbProperties") Properties couchdbProperties,
                                         @Qualifier("activemqProperties") Properties activemqProperties) {
        this.ananyaProperties = ananyaProperties;
        this.couchdbProperties = couchdbProperties;
        this.activemqProperties = activemqProperties;
    }

    @Diagnostic(name = "ananyaConfiguration")
    public DiagnosticsResult performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        logPropertiesFileFor(diagnosticLog, "ananya.properties", ananyaProperties);
        logPropertiesFileFor(diagnosticLog, "couchdb.properties", couchdbProperties);
        logPropertiesFileFor(diagnosticLog, "activemq.properties", activemqProperties);

        return new DiagnosticsResult(true, diagnosticLog.toString());
    }

    private void logPropertiesFileFor(DiagnosticLog diagnosticLog, String file, Properties properties) {
        diagnosticLog.add(file + ":\n");
        TreeSet sortedKeys = new TreeSet(properties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + properties.get(key));
        diagnosticLog.add("______________________________________________________________");
    }
}
