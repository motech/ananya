package org.motechproject.ananya.support.diagnostics;

import org.hibernate.classic.Session;
import org.hibernate.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

            DateTime today = DateTime.now();
            int registeredTodayCount = ((Long) session.createQuery(
                    "select count(*) from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                            " where flwd.id = rm.frontLineWorkerDimension.id" +
                            " and td.id = rm.timeDimension.id" +
                            " and td.day = " + today.getDayOfYear() +
                            " and td.month = " + today.getMonthOfYear() +
                            " and td.year = " + today.getYear()).uniqueResult()).intValue();
            diagnosticLog.add("Front Line Workers registered today are : " + registeredTodayCount);

        } catch (Exception e) {
            diagnosticLog.add("Opening session failed");
            diagnosticLog.add(ExceptionUtils.getFullStackTrace(e));
        }
        return diagnosticLog;
    }
}
