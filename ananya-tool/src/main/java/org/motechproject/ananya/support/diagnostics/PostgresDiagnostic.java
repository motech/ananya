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

            int flwCount = ((Long) session.createQuery("select count(*) from FrontLineWorkerDimension").uniqueResult()).intValue();
            diagnosticLog.add("Front Line Workers in database : " + flwCount);

            Iterator totalCountByRegisteredStatus;
            totalCountByRegisteredStatus = (session.createQuery(
                    "select count(*),status from FrontLineWorkerDimension group by status").list().iterator());
            diagnosticLog.add("\nFront Line Workers in database registered by registration status are : \n");
            while (totalCountByRegisteredStatus.hasNext()) {
                Object[] row = (Object[]) totalCountByRegisteredStatus.next();
                Long count = (Long) row[0];
                String status = (String) row[1];
                diagnosticLog.add(status + ":" + count + "\n");

            }

            DateTime today = DateTime.now();
            int registeredTodayCount = ((Long) session.createQuery(
                    "select count(*) from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                            " where flwd.id = rm.frontLineWorkerDimension.id" +
                            " and td.id = rm.timeDimension.id" +
                            " and td.day = " + today.getDayOfYear() +
                            " and td.month = " + today.getMonthOfYear() +
                            " and td.year = " + today.getYear()).uniqueResult()).intValue();
            diagnosticLog.add("\nFront Line Workers registered today are : " + registeredTodayCount);

            Iterator totalCountByStatusAndDate;
            totalCountByStatusAndDate = (session.createQuery(
                    "select count(*),flwd.status from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                            " where flwd.id = rm.frontLineWorkerDimension.id" +
                            " and td.id = rm.timeDimension.id" +
                            " and td.day = " + today.getDayOfYear() +
                            " and td.month = " + today.getMonthOfYear() +
                            " and td.year = " + today.getYear() +
                            " group by flwd.status")).list().iterator();
            diagnosticLog.add("\nFront Line Workers registered today by registration status are : \n");
            while (totalCountByStatusAndDate.hasNext()) {
                Object[] row = (Object[]) totalCountByStatusAndDate.next();
                Long count = (Long) row[0];
                String status = (String) row[1];
                diagnosticLog.add(status + ":" + count + "\n");
            }

            int jobAidCallCount = ((Long) session.createQuery("select count(distinct callId) from JobAidContentMeasure").uniqueResult()).intValue();
            diagnosticLog.add("\nTotal calls made to JobAid : " + jobAidCallCount);

            int jobAidCallCountForToday = ((Long) session.createQuery("select count(distinct jacm.callId) from JobAidContentMeasure jacm, TimeDimension td" +
                    " where td.id = jacm.timeDimension.id" +
                    " and td.day = " + today.getDayOfYear() +
                    " and td.month = " + today.getMonthOfYear() +
                    " and td.year = " + today.getYear()).uniqueResult()).intValue();
            diagnosticLog.add("\nTotal calls made today to JobAid : " + jobAidCallCountForToday);

            int certificateCourseCallCount = ((Long) session.createQuery("select count(distinct callId) from CourseItemMeasure").uniqueResult()).intValue();
            diagnosticLog.add("\nTotal calls made to Certificate Course : " + certificateCourseCallCount);

            int certificateCourseCallCountForToday = ((Long) session.createQuery("select count(distinct cim.callId) from CourseItemMeasure cim, TimeDimension td" +
                    " where td.id = cim.timeDimension.id" +
                    " and td.day = " + today.getDayOfYear() +
                    " and td.month = " + today.getMonthOfYear() +
                    " and td.year = " + today.getYear()).uniqueResult()).intValue();
            diagnosticLog.add("\nTotal calls made today to Certificate Course : " + certificateCourseCallCountForToday);

            int smsTotalCount =  ((Long) session.createQuery("select count(*) from SMSSentMeasure " +
                    "where smsSent = true").uniqueResult()).intValue();
            diagnosticLog.add("\nTotal SMS sent : " + smsTotalCount);


            int smsTotalCountForToday =  ((Long) session.createQuery("select count(*) from SMSSentMeasure ssm, TimeDimension td" +
                    " where ssm.smsSent = true" +
                    " and td.id = ssm.timeDimension.id" +
                    " and td.day = " + today.getDayOfYear() +
                    " and td.month = " + today.getMonthOfYear() +
                    " and td.year = " + today.getYear()).uniqueResult()).intValue();
            diagnosticLog.add("\nTotal SMS sent today: " + smsTotalCountForToday);


        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        }
        return diagnosticLog;
    }
}
