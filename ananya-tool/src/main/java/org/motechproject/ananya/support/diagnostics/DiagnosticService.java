package org.motechproject.ananya.support.diagnostics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagnosticService {

    @Autowired
    private ActiveMQDiagnostic activeMQDiagnostic;
    @Autowired
    private CouchDBDiagnostic couchDBDiagnostic;
    @Autowired
    private PostgresDiagnostic postgresDiagnostic;

    public String getDiagnostics() throws Exception {
        StringBuilder sb = new StringBuilder();
        DiagnosticLog diagnosticLog;

        diagnosticLog = couchDBDiagnostic.performDiagnosis();
        sb.append(diagnosticLog.toString());
        diagnosticLog = activeMQDiagnostic.performDiagnosis();
        sb.append(diagnosticLog.toString());
        diagnosticLog = postgresDiagnostic.performDiagnosis();
        sb.append(diagnosticLog.toString());

        return sb.toString();
    }

}
