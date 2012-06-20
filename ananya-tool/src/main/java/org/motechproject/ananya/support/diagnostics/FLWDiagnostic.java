package org.motechproject.ananya.support.diagnostics;

import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticQuery;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class FLWDiagnostic {

    @Autowired
    private DataAccessTemplate dataAccessTemplate;

    @Diagnostic(name = "flwDiagnostics")
    public DiagnosticsResult performDiagnosis() {
        boolean isSuccess = true;
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        diagnosticLog.add("Opening session with database");
        try {
            Session session = dataAccessTemplate.getSessionFactory().openSession();
            flwDiagnosis(diagnosticLog, session);
            jobAidDiagnosis(diagnosticLog, session);
            certificateCourseDiagnosis(diagnosticLog, session);
            SMSSentDiagnosis(diagnosticLog, session);
        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.addError(e);
            isSuccess = false;
        }
        return new DiagnosticsResult(isSuccess, diagnosticLog.toString());
    }

    private void flwDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int flwTotalCount = getCountFor(session, (DiagnosticQuery.FIND_TOTAL_FLWS).getQuery());
        diagnosticLog.add("FrontlineWorkers in database : " + flwTotalCount);

        int flwRegisteredTodayCount = getCountFor(session, DiagnosticQuery.FIND_FLWS_REG_TODAY.getQuery(today));
        diagnosticLog.add("FrontlineWorkers registered today are : " + flwRegisteredTodayCount);

        Iterator totalCountByRegisteredStatus = (session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS.getQuery()).list().iterator());
        diagnosticLog.add("FrontlineWorkers registered by registration status are : ");
        while (totalCountByRegisteredStatus.hasNext()) {
            Object[] row = (Object[]) totalCountByRegisteredStatus.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            diagnosticLog.add(status + ":" + count + "");
        }

        Iterator totalCountByStatusAndDate = (session.createQuery(DiagnosticQuery.FIND_FLWS_BY_STATUS_TODAY.getQuery(today))).list().iterator();
        diagnosticLog.add("FrontlineWorkers registered today by registration status are : ");
        while (totalCountByStatusAndDate.hasNext()) {
            Object[] row = (Object[]) totalCountByStatusAndDate.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            diagnosticLog.add(status + ":" + count + "");
        }
    }

    private void jobAidDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int jobAidCallCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS.getQuery());
        diagnosticLog.add("Total calls made to JobAid : " + jobAidCallCount);

        int jobAidCallCountForToday = getCountFor(session, DiagnosticQuery.FIND_JOB_AID_CALLS_TODAY.getQuery(today));
        diagnosticLog.add("Total calls made today to JobAid : " + jobAidCallCountForToday);
    }

    private void certificateCourseDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int certificateCourseCallCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_CCOURSE_CALLS.getQuery());
        diagnosticLog.add("Total calls made to Certificate Course : " + certificateCourseCallCount);

        int certificateCourseCallCountForToday = getCountFor(session,DiagnosticQuery.FIND_CCOURSE_CALLS_TODAY.getQuery(today));
        diagnosticLog.add("Total calls made today to Certificate Course : " + certificateCourseCallCountForToday);
    }

    private void SMSSentDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int smsTotalCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_SMS_SENT.getQuery());
        diagnosticLog.add("Total SMS sent : " + smsTotalCount);

        int smsTotalCountForToday = getCountFor(session, DiagnosticQuery.FIND_SMS_SENT_TODAY.getQuery(today));
        diagnosticLog.add("Total SMS sent today: " + smsTotalCountForToday);
    }

    private int getCountFor(Session session, String query) {
        return ((Long) session.createQuery(query).uniqueResult()).intValue();
    }
}
