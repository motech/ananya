package org.motechproject.ananya.support.diagnostics;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.event.MotechEventConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.TreeSet;

@Component
public class AnanyaConfigurationDiagnostic {

    private Properties ananyaProperties;
    private Properties couchdbProperties;
    private Properties activemqProperties;
    private HttpClientFactoryBean httpClient;
    private CachingConnectionFactory connectionFactory;
    private Queue eventQueue;
    private Queue schedulerQueue;
    private MotechEventConfig motechEventConfig;

    @Autowired
    public AnanyaConfigurationDiagnostic(@Qualifier("ananyaProperties") Properties ananyaProperties,
                                         @Qualifier("couchdbProperties") Properties couchdbProperties,
                                         @Qualifier("activemqProperties") Properties activemqProperties,
                                         @Qualifier("httpClient") HttpClientFactoryBean httpClient,
                                         CachingConnectionFactory connectionFactory,
                                         Queue eventQueue,
                                         Queue schedulerQueue,
                                         MotechEventConfig motechEventConfig) {
        this.ananyaProperties = ananyaProperties;
        this.couchdbProperties = couchdbProperties;
        this.activemqProperties = activemqProperties;
        this.httpClient = httpClient;
        this.connectionFactory = connectionFactory;
        this.eventQueue = eventQueue;
        this.schedulerQueue = schedulerQueue;
        this.motechEventConfig = motechEventConfig;
    }

    @Diagnostic(name = "ananyaConfiguration")
    public DiagnosticsResult performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        logPropertiesFileFor(diagnosticLog, "ananya.properties", ananyaProperties);
        logPropertiesForCouchDb(diagnosticLog);
        logPropertiesForActiveMQ(diagnosticLog);

        return new DiagnosticsResult(true, diagnosticLog.toString());
    }

    private void logPropertiesFileFor(DiagnosticLog diagnosticLog, String file, Properties properties) {
        diagnosticLog.add(file + ":\n");
        TreeSet sortedKeys = new TreeSet(properties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + properties.get(key));
        diagnosticLog.add("______________________________________________________________");
    }

    private void logPropertiesForCouchDb(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("\nFrom couchdb.properties file:");
        diagnosticLog.add("-----------------------------");
        TreeSet sortedKeys = new TreeSet(couchdbProperties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + couchdbProperties.get(key));

        diagnosticLog.add("\nActual values:");
        diagnosticLog.add("---------------");
        try {
            for (Object key : sortedKeys) {
                Field keyField = HttpClientFactoryBean.class.getDeclaredField((String) key);
                keyField.setAccessible(true);
                diagnosticLog.add(key + "=" + keyField.get(httpClient));
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        diagnosticLog.add("______________________________________________________________");
    }

    private void logPropertiesForActiveMQ(DiagnosticLog diagnosticLog) throws JMSException {
        diagnosticLog.add("\nFrom activemq.properties file:");
        diagnosticLog.add("------------------------------");
        TreeSet sortedKeys = new TreeSet(activemqProperties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + activemqProperties.get(key));

        diagnosticLog.add("\nActual values:");
        diagnosticLog.add("--------------");
        diagnosticLog.add("queue.for.events=" + eventQueue.getQueueName());
        diagnosticLog.add("queue.for.scheduler=" + schedulerQueue.getQueueName());
        ActiveMQConnectionFactory targetConnectionFactory = (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
        diagnosticLog.add("broker.url=" + targetConnectionFactory.getProperties().get("brokerURL"));
        diagnosticLog.add("maximumRedeliveries=" + targetConnectionFactory.getRedeliveryPolicy().getMaximumRedeliveries());
        diagnosticLog.add("redeliveryDelayInMillis=" + targetConnectionFactory.getRedeliveryPolicy().getRedeliveryDelay());
        diagnosticLog.add("motech.message.max.redelivery.count=" + motechEventConfig.getMessageMaxRedeliveryCount());
        diagnosticLog.add("motech.message.redelivery.delay=" + motechEventConfig.getMessageRedeliveryDelay());

        diagnosticLog.add("______________________________________________________________");
    }
}
