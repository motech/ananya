package org.motechproject.ananya.support.diagnostics;

import org.hibernate.classic.Session;
import org.hibernate.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class PostgresDiagnostic implements Diagnostic {

    @Autowired
    private DataAccessTemplate dataAccessTemplate;

    @Override
    public DiagnosticLog performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog("postgres");
        diagnosticLog.add("Opening session with database");
        try {
            Session session = dataAccessTemplate.getSessionFactory().openSession();
            FLWDiagnosis(diagnosticLog, session);
            jobAidDiagnosis(diagnosticLog, session);
            certificateCourseDiagnosis(diagnosticLog, session);
            SMSSentDiagnosis(diagnosticLog, session);
        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        }
        return diagnosticLog;
    }

    private void FLWDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int flwTotalCount = getCountFor(session, (DiagnosticQuery.FIND_TOTAL_FLWS).getQuery());
        diagnosticLog.add("\nFront Line Workers in database : " + flwTotalCount);

        int flwRegisteredTodayCount = getCountFor(session, DiagnosticQuery.FIND_FLWS_REG_TODAY.getQuery(today));
        diagnosticLog.add("\nFront Line Workers registered today are : " + flwRegisteredTodayCount);

        Iterator totalCountByRegisteredStatus = (session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS.getQuery()).list().iterator());
        diagnosticLog.add("\nFront Line Workers in database registered by registration status are : \n");
        while (totalCountByRegisteredStatus.hasNext()) {
            Object[] row = (Object[]) totalCountByRegisteredStatus.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            diagnosticLog.add(status + ":" + count + "\n");
        }

        Iterator totalCountByStatusAndDate = (session.createQuery(DiagnosticQuery.FIND_FLWS_BY_STATUS_TODAY.getQuery(today))).list().iterator();
        diagnosticLog.add("\nFront Line Workers registered today by registration status are : \n");
        while (totalCountByStatusAndDate.hasNext()) {
            Object[] row = (Object[]) totalCountByStatusAndDate.next();
            Long count = (Long) row[0];
            String status = (String) row[1];
            diagnosticLog.add(status + ":" + count + "\n");
        }
    }

    private void jobAidDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int jobAidCallCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS.getQuery());
        diagnosticLog.add("\nTotal calls made to JobAid : " + jobAidCallCount);

        int jobAidCallCountForToday = getCountFor(session, DiagnosticQuery.FIND_JOB_AID_CALLS_TODAY.getQuery(today));
        diagnosticLog.add("\nTotal calls made today to JobAid : " + jobAidCallCountForToday);
    }

    private void certificateCourseDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int certificateCourseCallCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_CCOURSE_CALLS.getQuery());
        diagnosticLog.add("\nTotal calls made to Certificate Course : " + certificateCourseCallCount);

        int certificateCourseCallCountForToday = getCountFor(session,DiagnosticQuery.FIND_CCOURSE_CALLS_TODAY.getQuery(today));
        diagnosticLog.add("\nTotal calls made today to Certificate Course : " + certificateCourseCallCountForToday);
    }

    private void SMSSentDiagnosis(DiagnosticLog diagnosticLog, Session session) {
        DateTime today = DateTime.now() ;
        int smsTotalCount = getCountFor(session, DiagnosticQuery.FIND_TOTAL_SMS_SENT.getQuery());
        diagnosticLog.add("\nTotal SMS sent : " + smsTotalCount);

        int smsTotalCountForToday = getCountFor(session, DiagnosticQuery.FIND_SMS_SENT_TODAY.getQuery(today));
        diagnosticLog.add("\nTotal SMS sent today: " + smsTotalCountForToday);
    }

    private int getCountFor(Session session, String query) {
        return ((Long) session.createQuery(query).uniqueResult()).intValue();
    }
}
