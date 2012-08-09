package org.motechproject.ananya.support.diagnostics;

import org.apache.commons.lang.StringUtils;
import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticQuery;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class PostgresDataDiagnostic {

    @Autowired
    private DataAccessTemplate dataAccessTemplate;

    @Diagnostic(name = "postgresDataDiagnostics")
    public DiagnosticsResult performDiagnosis() {
        DiagnosticLog diagnosticLog = diagnose();
        DiagnosticsResult diagnosticsResult = new DiagnosticsResult(true, diagnosticLog.toString());
        return diagnosticsResult;
    }

    public Map<String, String> collect() {
        Session session = dataAccessTemplate.getSessionFactory().openSession();
        Map<String, String> results = new LinkedHashMap<String, String>();
        try {
            results.putAll(runQueries(session, DiagnosticQuery.FIND_TOTAL_FLWS, DiagnosticQuery.FIND_TODAY_FLWS));
            results.putAll(runGroupQueries(session, DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS, DiagnosticQuery.FIND_TODAY_FLWS_BY_STATUS));
            results.putAll(runQueries(session, DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS, DiagnosticQuery.FIND_TODAY_JOB_AID_CALLS));
            results.putAll(runQueries(session, DiagnosticQuery.FIND_TOTAL_COURSE_CALLS, DiagnosticQuery.FIND_TODAY_COURSE_CALLS));
            results.putAll(runQueries(session, DiagnosticQuery.FIND_TOTAL_SMS_SENT, DiagnosticQuery.FIND_TODAY_SMS_SENT));
        } finally {
            session.close();
        }
        return results;
    }

    private Map<String, String> runQueries(Session session, DiagnosticQuery totalCountQuery, DiagnosticQuery todayCountQuery) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        String totalCount = session.createQuery(totalCountQuery.getQuery()).uniqueResult().toString();
        results.put(totalCountQuery.title(), totalCount);

        String todayCount = session.createQuery(todayCountQuery.getQuery(DateTime.now())).uniqueResult().toString();
        results.put(todayCountQuery.title(), todayCount);
        return results;
    }

    private DiagnosticLog diagnose() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("POSTGRES");
        diagnosticLog.add("Opening session with database");
        try {
            diagnosticLog.add(collect());
        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.addError(e);
        }
        return diagnosticLog;
    }

    private Map<String, String> runGroupQueries(Session session, DiagnosticQuery totalCountQuery, DiagnosticQuery todayCountQuery) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        Iterator totalCount = (session.createQuery(totalCountQuery.getQuery()).list().iterator());
        iterateAndAdd(totalCountQuery, results, totalCount);

        Iterator todayCount = (session.createQuery(todayCountQuery.getQuery(DateTime.now())).list().iterator());
        iterateAndAdd(todayCountQuery, results, todayCount);
        return results;
    }

    private void iterateAndAdd(DiagnosticQuery query, Map<String, String> results, Iterator iterator) {
        List<String> statusList = new ArrayList<String>();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            statusList.add(status + ": " + count);
        }
        Collections.sort(statusList);
        results.put(query.title(), StringUtils.join(statusList," | "));
    }
}
