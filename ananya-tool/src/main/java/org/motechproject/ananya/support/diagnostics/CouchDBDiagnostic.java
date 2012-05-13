package org.motechproject.ananya.support.diagnostics;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CouchDBDiagnostic implements Diagnostic {

    @Autowired
    @Qualifier("ananyaDbInstance")
    private StdCouchDbInstance ananyaDBInstance;

    @Value("#{couchdbProperties['host']}")
    private String server;

    @Value("#{couchdbProperties['port']}")
    private String port;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("COUCHDB");
        diagnosticLog.add("Checking couch db connection");
        try {
            ananyaDBInstance.getConnection().head("/");
            diagnosticLog.add("Databases present : " + ananyaDBInstance.getAllDatabases().toString());

            HttpClient httpClient = new HttpClient();
            for (DiagnosticUrl diagnosticUrl : DiagnosticUrl.values()) {
                GetMethod method = new GetMethod(diagnosticUrl.getFor(server, port));
                httpClient.executeMethod(method);
                String httpResponse = method.getResponseBodyAsString();
                diagnosticLog.add(diagnosticUrl.description()+" : "+httpResponse);
            }

        } catch (Exception e) {
            diagnosticLog.add("Couch DB connection failed");
            diagnosticLog.addError(e);
        }
        return diagnosticLog;
    }
}
