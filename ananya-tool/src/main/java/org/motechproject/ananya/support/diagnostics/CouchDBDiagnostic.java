package org.motechproject.ananya.support.diagnostics;


import org.ektorp.impl.StdCouchDbInstance;
import org.hibernate.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CouchDBDiagnostic implements Diagnostic {

    @Autowired
    @Qualifier("ananyaDbInstance")
    private StdCouchDbInstance ananyaDBInstance;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("couchdb");
        diagnosticLog.add("Checking couch db connection");
        try {
            ananyaDBInstance.getConnection().head("/");
            diagnosticLog.add(ananyaDBInstance.getAllDatabases().toString());
        } catch (Exception e) {
            diagnosticLog.add("Couch DB connection failed");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        }
        return diagnosticLog;
    }
}
