package org.motechproject.ananya.support.diagnostics;


import org.ektorp.impl.StdCouchDbInstance;
import org.hibernate.exception.ExceptionUtils;
import org.motechproject.ananya.repository.*;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.model.MotechBaseDataObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CouchDBDiagnostic implements Diagnostic {

    @Autowired
    @Qualifier("ananyaDbInstance")
    private StdCouchDbInstance ananyaDBInstance;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;
    @Autowired
    private AllCallLogs allCallLogs;
    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;
    @Autowired
    private AllOperators allOperators;
    @Autowired
    private AllSMSReferences allSMSReferences;
    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("COUCHDB");
        diagnosticLog.add("Checking couch db connection");
        try {
            ananyaDBInstance.getConnection().head("/");
            diagnosticLog.add("Databases present : " + ananyaDBInstance.getAllDatabases().toString());

            List<MotechBaseRepository<? extends MotechBaseDataObject>> repositories =
                    Arrays.asList(allFrontLineWorkers, allLocations, allOperators, allCallLogs,
                            allCertificateCourseLogs, allAudioTrackerLogs, allSMSReferences);

            for (MotechBaseRepository<? extends MotechBaseDataObject> repository : repositories) {
                List<? extends MotechBaseDataObject> dataObjects = repository.getAll();
                diagnosticLog.add("Size of " + repository.getClass().getSimpleName() + ": " + dataObjects.size());
                dataObjects.clear();
            }

        } catch (Exception e) {
            diagnosticLog.add("Couch DB connection failed");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        }
        return diagnosticLog;
    }
}
