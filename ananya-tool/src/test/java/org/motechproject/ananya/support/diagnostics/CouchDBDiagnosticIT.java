package org.motechproject.ananya.support.diagnostics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class CouchDBDiagnosticIT {

    @Autowired
    CouchDBDiagnostic couchDBDiagnostic;

    @Test
    public void shouldPerformCouchDBDiagnostic() {
        String diagnosticLog = couchDBDiagnostic.performDiagnosis().getMessage();
        System.out.println(diagnosticLog);
        assertNotNull(diagnosticLog);
    }
}
