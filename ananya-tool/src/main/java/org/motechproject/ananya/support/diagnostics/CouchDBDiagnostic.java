package org.motechproject.ananya.support.diagnostics;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            Map<String, String> results = collect();
            for (String result : results.keySet())
                diagnosticLog.add(result + " : " + results.get(result));

        } catch (Exception e) {
            diagnosticLog.add("Couch DB connection failed");
            diagnosticLog.addError(e);
        }
        return diagnosticLog;
    }

    public Map<String, String> collect() throws IOException {
        HttpClient httpClient = new HttpClient();
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (DiagnosticUrl diagnosticUrl : DiagnosticUrl.values()) {
            GetMethod method = new GetMethod(diagnosticUrl.getFor(server, port));
            httpClient.executeMethod(method);
            results.put(getDescription(diagnosticUrl), getCountFrom(method.getResponseBodyAsString()));
        }
        return results;
    }

    public String getCountFrom(String responseBody) {
        Pattern r = Pattern.compile("(\\d+)");
        Matcher m = r.matcher(responseBody);
        return m.find() && m.groupCount() != 0 ? m.group(0) : responseBody;
    }

    private String getDescription(DiagnosticUrl diagnosticUrl) {
        return StringUtils.remove(diagnosticUrl.description(), "Total number of");
    }
}
