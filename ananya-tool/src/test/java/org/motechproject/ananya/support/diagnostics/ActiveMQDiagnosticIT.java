package org.motechproject.ananya.support.diagnostics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class ActiveMQDiagnosticIT {

    @Autowired
    ActiveMQDiagnostic activeMQDiagnostic;

    @Test
    public void shouldCheckActiveMQConnection() throws JMSException {
        DiagnosticLog diagnosticLog = activeMQDiagnostic.performDiagnosis();
        System.out.println(diagnosticLog.toString());
        assertNotNull(diagnosticLog);
    }
}
