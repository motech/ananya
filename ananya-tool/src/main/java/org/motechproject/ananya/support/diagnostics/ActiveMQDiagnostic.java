package org.motechproject.ananya.support.diagnostics;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.hibernate.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

@Component
public class ActiveMQDiagnostic implements Diagnostic{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private ActiveMQQueue eventQueue;

    private Connection connection;
    private Session session;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("activemq");

        try {
            diagnosticLog.add("Checking for Active MQ connection");

            ActiveMQConnectionFactory activeMQConnectionFactory =
                    (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            diagnosticLog.add("Successfully opened connection");
        } catch (JMSException e) {
            if (connection != null) try {
                connection.close();
            } catch (JMSException e1) {
                diagnosticLog.add("Error in creating ActiveMQ connection");
                diagnosticLog.add(ExceptionUtils.getFullStackTrace(e1));
            }

            return diagnosticLog;
        }

        diagnosticLog.add("Opening browser for queue size");
        QueueBrowser browser = null;
        int queueSize = 0;
        try {
            browser = session.createBrowser(eventQueue);

            Enumeration messages = null;
                messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                messages.nextElement();
                queueSize++;
            }
        } catch (JMSException e) {
            try {
                browser.close();
                connection.close();
            } catch (JMSException e1) {
            }
        }

        diagnosticLog.add("Queue size is : " + queueSize);

        return diagnosticLog;
    }
}
