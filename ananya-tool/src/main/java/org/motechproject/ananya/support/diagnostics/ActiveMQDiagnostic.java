package org.motechproject.ananya.support.diagnostics;

import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class ActiveMQDiagnostic implements Diagnostic {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Override
    public DiagnosticLog performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog("ACTIVEMQ");
        try {
            diagnosticLog.add("Checking for ActiveMQ connection");
            connectionFactory.getTargetConnectionFactory().createConnection().start();
            diagnosticLog.add("ActiveMQ connection is good");
        } catch (Exception e) {
            diagnosticLog.add("Error in creating ActiveMQ connection");
            diagnosticLog.addError(e);
        }
        return diagnosticLog;
    }

}
