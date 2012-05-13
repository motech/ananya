package org.motechproject.ananya.support.diagnostics;

import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.Properties;
import java.util.TreeSet;

@Component
public class ConfigurationDiagnostic implements Diagnostic {

    @Autowired
    @Qualifier("ananyaProperties")
    private Properties ananyaProperties;

    @Autowired
    @Qualifier("couchdbProperties")
    private Properties couchdbProperties;

    @Autowired
    @Qualifier("activemqProperties")
    private Properties activemqProperties;

    @Override
    public DiagnosticLog performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog("CONFIGURATION");

        logPropertiesFileFor(diagnosticLog, "ananya.properties", ananyaProperties);
        logPropertiesFileFor(diagnosticLog, "couchdb.properties", couchdbProperties);
        logPropertiesFileFor(diagnosticLog, "activemq.properties", activemqProperties);

        return diagnosticLog;
    }

    private void logPropertiesFileFor(DiagnosticLog diagnosticLog, String file, Properties properties) {
        diagnosticLog.add(file + ":\n");
        TreeSet sortedKeys = new TreeSet(properties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + properties.get(key));
        diagnosticLog.add("______________________________________________________________");
    }
}
