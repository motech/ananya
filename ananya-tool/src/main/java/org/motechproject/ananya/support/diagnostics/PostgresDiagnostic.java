package org.motechproject.ananya.support.diagnostics;

import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.support.diagnostics.base.Diagnostic;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PostgresDiagnostic implements Diagnostic {

    @Autowired
    private DataAccessTemplate dataAccessTemplate;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("POSTGRES");
        diagnosticLog.add("Opening session with database");
        Session session = dataAccessTemplate.getSessionFactory().openSession();
        try {
            addToLog(diagnosticLog, getResultsFor(session, DiagnosticQuery.FIND_TOTAL_FLWS, DiagnosticQuery.FIND_FLWS_REG_TODAY));
            Map<String, String> groupResults = getGroupResultsFor(session, DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS, DiagnosticQuery.FIND_FLWS_BY_STATUS_TODAY);
            for (String key : groupResults.keySet())
                diagnosticLog.add(key + ":" + groupResults.get(key));

            addToLog(diagnosticLog, getResultsFor(session, DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS, DiagnosticQuery.FIND_TODAY_JOB_AID_CALLS));
            addToLog(diagnosticLog, getResultsFor(session, DiagnosticQuery.FIND_TOTAL_CCOURSE_CALLS, DiagnosticQuery.FIND_TODAY_CCOURSE_CALLS));
            addToLog(diagnosticLog, getResultsFor(session, DiagnosticQuery.FIND_TOTAL_SMS_SENT, DiagnosticQuery.FIND_TODAY_SMS_SENT));
        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.addError(e);
        } finally {
            session.close();
        }
        return diagnosticLog;
    }

    private Map<String, Integer> getResultsFor(Session session, DiagnosticQuery totalCountQuery, DiagnosticQuery todayCountQuery) {
        Map<String, Integer> results = new HashMap<String, Integer>();
        results.put(totalCountQuery.getDescription(), getCountFor(session, totalCountQuery.getQuery()));
        results.put(todayCountQuery.getDescription(), getCountFor(session, todayCountQuery.getQuery(DateTime.now())));
        return results;
    }

    private Map<String, String> getGroupResultsFor(Session session, DiagnosticQuery totalCountQuery, DiagnosticQuery todayCountQuery) {
        Map<String, String> results = new HashMap<String, String>();
        Iterator totalCount = (session.createQuery(totalCountQuery.getQuery()).list().iterator());
        iterateAndAdd(totalCountQuery, results, totalCount);

        Iterator todayCount = (session.createQuery(todayCountQuery.getQuery(DateTime.now())).list().iterator());
        iterateAndAdd(todayCountQuery, results, todayCount);
        return results;
    }

    private int getCountFor(Session session, String query) {
        return ((Long) session.createQuery(query).uniqueResult()).intValue();
    }

    private void addToLog(DiagnosticLog diagnosticLog, Map<String, Integer> resultMap) {
        for (String key : resultMap.keySet())
            diagnosticLog.add(key + ":" + resultMap.get(key));
    }

    private void iterateAndAdd(DiagnosticQuery query, Map<String, String> results, Iterator iterator) {
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            results.put(query.getDescription(), status + ":" + count + "");
        }
    }
}
