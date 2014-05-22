package org.motechproject.ananya.support.diagnostics;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.ektorp.spring.HttpClientFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEventConfig;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Queue;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;

public class AnanyaConfigurationDiagnosticTest {

    private Properties activemqProperties = new Properties();
    private Properties couchdbProperties = new Properties();
    private Properties ananyaProperties = new Properties();

    private AnanyaConfigurationDiagnostic diagnostic;
    private HttpClientFactoryBean httpClient = new HttpClientFactoryBean();
    private CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    private Queue eventQueue = new ActiveMQQueue();
    private Queue schedulerQueue = new ActiveMQQueue();
    private MotechEventConfig motechEventConfig = new MotechEventConfig();
    private ComboPooledDataSource dataSource = new ComboPooledDataSource();

    @Before
    public void setUp() {
        diagnostic = new AnanyaConfigurationDiagnostic(ananyaProperties, couchdbProperties, activemqProperties, httpClient, connectionFactory, eventQueue, schedulerQueue, motechEventConfig, dataSource);
    }

    @Test
    public void shouldPrintAllPropertiesToLog() throws Exception {
        activemqProperties.put("A1", "A1Value");
        activemqProperties.put("A2", "A2Value");

        ananyaProperties.put("AN1", "AN1Value");
        ananyaProperties.put("AN2", "AN2Value");

        couchdbProperties.put("host", "localhost3");
        httpClient.setProperties(couchdbProperties);
        httpClient.afterPropertiesSet();

        connectionFactory.setTargetConnectionFactory(new ActiveMQConnectionFactory());
        ActiveMQConnectionFactory targetConnectionFactory = (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
        targetConnectionFactory.setBrokerURL("tcp://localhost:61616?jms.prefetchPolicy.all=0");

        dataSource.setJdbcUrl("jdbc:postgresql://localhost/ananya/");


        String diagnosticLog = diagnostic.performDiagnosis().getMessage();


        assertTrue(diagnosticLog.contains("AN1=AN1Value"));
        assertTrue(diagnosticLog.contains("AN2=AN2Value"));
        assertTrue(diagnosticLog.contains("A1=A1Value"));
        assertTrue(diagnosticLog.contains("A2=A2Value"));

        assertTrue(diagnosticLog.contains("host=localhost3"));
        assertTrue(diagnosticLog.contains("broker.url=tcp://localhost:61616"));

        assertTrue(diagnosticLog.contains("jdbc.url=jdbc:postgresql://localhost/ananya/"));
    }
}
