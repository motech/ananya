package org.motechproject.ananya.support.diagnostics;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.hibernate.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

@Component
public class ActiveMQDiagnostic implements Diagnostic {

    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private ActiveMQQueue eventQueue;

    private Connection connection;
    private Session session;
    private QueueBrowser browser;

    @Override
    public DiagnosticLog performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog("activemq");
        try {
            diagnosticLog.add("Checking for Active MQ connection");
            ActiveMQConnectionFactory activeMQConnectionFactory =
                    (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            diagnosticLog.add("Successfully opened connection");

            diagnosticLog.add("Opening browser for queue size");
            int queueSize = 0;
            browser = session.createBrowser(eventQueue);
            Enumeration messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                messages.nextElement();
                queueSize++;
            }
            diagnosticLog.add("Queue size is : " + queueSize);
        } catch (Exception e) {
            diagnosticLog.add("Error in creating ActiveMQ connection");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (browser != null) browser.close();
            if (connection != null) connection.close();
        }
        return diagnosticLog;
    }
}