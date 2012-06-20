package org.motechproject.ananya.support.diagnostics;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticUrl;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CouchDBDiagnostic {

    @Autowired
    @Qualifier("ananyaDbInstance")
    private StdCouchDbInstance ananyaDBInstance;

    @Value("#{couchdbProperties['host']}")
    private String server;

    @Value("#{couchdbProperties['port']}")
    private String port;

    @Diagnostic(name = "couchDb")
    public DiagnosticsResult performDiagnosis() {
        boolean isSuccess = true;
        DiagnosticLog diagnosticLog = new DiagnosticLog();
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
            isSuccess = false;
        }
        return new DiagnosticsResult(isSuccess, diagnosticLog.toString());
    }
}
