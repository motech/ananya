package org.motechproject.ananya.support.diagnostics;

import org.joda.time.DateTime;

public enum DiagnosticQuery {

    FIND_TOTAL_FLWS("select count(*) from FrontLineWorkerDimension"),

    FIND_FLWS_REG_TODAY(
            " select count(*) from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id" +
                    " and td.day =  %s and td.month = %s and td.year = %s"
    ),
    FIND_TOTAL_FLWS_BY_STATUS("select count(*),status from FrontLineWorkerDimension group by status"),

    FIND_FLWS_BY_STATUS_TODAY(
            " select count(*),flwd.status from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td " +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s" +
                    " group by flwd.status"
    ),
    FIND_TOTAL_JOB_AID_CALLS("select count(distinct callId) from JobAidContentMeasure"),
    FIND_JOB_AID_CALLS_TODAY(
            " select count(distinct jacm.callId) from JobAidContentMeasure jacm, TimeDimension td" +
                    " where td.id = jacm.timeDimension.id" +
                    " and td.day = %s and td.month = %s and td.year = %s"
    ),
    FIND_TOTAL_CCOURSE_CALLS("select count(distinct callId) from CourseItemMeasure"),
    FIND_CCOURSE_CALLS_TODAY(
            " select count(distinct cim.callId) from CourseItemMeasure cim, TimeDimension td " +
                    " where td.id = cim.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s"
    ),
    FIND_TOTAL_SMS_SENT("select count(*) from SMSSentMeasure where smsSent = true"),
    FIND_SMS_SENT_TODAY(
            "select count(*) from SMSSentMeasure ssm, TimeDimension td " +
                    " where ssm.smsSent = true " +
                    " and td.id = ssm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s"
    );
    private String query;

    private DiagnosticQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public String getQuery(DateTime today) {
        return (String.format(query, today.getDayOfYear(), today.getMonthOfYear(), today.getYear()));
    }

}
