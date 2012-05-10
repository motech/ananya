package org.motechproject.ananya.support.diagnostics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DiagnosticService {

    @Autowired
    private ActiveMQDiagnostic activeMQDiagnostic;
    @Autowired
    private CouchDBDiagnostic couchDBDiagnostic;
    @Autowired
    private PostgresDiagnostic postgresDiagnostic;
    @Autowired
    private ConfigurationDiagnostic configurationDiagnostic;


    public String getDiagnostics() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Diagnostic diagnostic : Arrays.asList(activeMQDiagnostic, couchDBDiagnostic, postgresDiagnostic, configurationDiagnostic))
            sb.append(diagnostic.performDiagnosis().toString());
        return sb.toString();
    }

}
