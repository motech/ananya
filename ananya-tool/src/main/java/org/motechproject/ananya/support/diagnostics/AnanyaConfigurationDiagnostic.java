package org.motechproject.ananya.support.diagnostics;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.event.MotechEventConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

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
    private ComboPooledDataSource dataSource;

    @Autowired
    public AnanyaConfigurationDiagnostic(@Qualifier("ananyaProperties") Properties ananyaProperties,
                                         @Qualifier("couchdbProperties") Properties couchdbProperties,
                                         @Qualifier("activemqProperties") Properties activemqProperties,
                                         @Qualifier("httpClient") HttpClientFactoryBean httpClient,
                                         CachingConnectionFactory connectionFactory,
                                         Queue eventQueue,
                                         Queue schedulerQueue,
                                         MotechEventConfig motechEventConfig,
                                         ComboPooledDataSource dataSource) {
        this.ananyaProperties = ananyaProperties;
        this.couchdbProperties = couchdbProperties;
        this.activemqProperties = activemqProperties;
        this.httpClient = httpClient;
        this.connectionFactory = connectionFactory;
        this.eventQueue = eventQueue;
        this.schedulerQueue = schedulerQueue;
        this.motechEventConfig = motechEventConfig;
        this.dataSource = dataSource;
    }

    @Diagnostic(name = "ananyaConfiguration")
    public DiagnosticsResult performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        logPropertiesForAnanya(diagnosticLog);
        logPropertiesForCouchDb(diagnosticLog);
        logPropertiesForActiveMQ(diagnosticLog);
        logPropertiesForPostgres(diagnosticLog);

        return new DiagnosticsResult(true, diagnosticLog.toString());
    }

    private void logPropertiesForPostgres(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("\nActual values for postgres configuration:");
        diagnosticLog.add("-----------------------------------------");

        diagnosticLog.add("jdbc.driverClassName=" + dataSource.getDriverClass());
        diagnosticLog.add("jdbc.url=" + dataSource.getJdbcUrl());
        diagnosticLog.add("jdbc.username=" + dataSource.getUser());
        diagnosticLog.add("hibernate.c3p0.max_size=" + dataSource.getMaxPoolSize());
        diagnosticLog.add("hibernate.c3p0.min_size=" + dataSource.getMinPoolSize());
        diagnosticLog.add("hibernate.c3p0.timeout=" + dataSource.getCheckoutTimeout());
        diagnosticLog.add("hibernate.c3p0.max_statements=" + dataSource.getMaxStatements());
        diagnosticLog.add("hibernate.c3p0.idle_test_period=" + dataSource.getIdleConnectionTestPeriod());
        diagnosticLog.add("hibernate.c3p0.acquire_increment=" + dataSource.getAcquireIncrement());

        diagnosticLog.add("______________________________________________________________");
    }

    private void logPropertiesForAnanya(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("\nFrom ananya.properties file:");
        diagnosticLog.add("----------------------------");
        TreeSet sortedKeys = new TreeSet(ananyaProperties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + ananyaProperties.get(key));
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
