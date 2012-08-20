package org.motechproject.ananya.support.diagnostics.base;

import org.joda.time.DateTime;

public enum DiagnosticQuery {

    FIND_TOTAL_FLWS("select count(*) from FrontLineWorkerDimension", "FLWs"),


    FIND_TODAY_FLWS(
            " select count(*) from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id" +
                    " and td.day =  %s and td.month = %s and td.year = %s",
            "FLWs Called Today"),

    FIND_TOTAL_FLWS_BY_STATUS("select count(*),status from FrontLineWorkerDimension group by status", "FLWs by Status"),


    FIND_TODAY_FLWS_BY_STATUS(
            " select count(*),flwd.status from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td " +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s" +
                    " group by flwd.status",
            "FLWs Called Today By Status"),

    FIND_TOTAL_JOB_AID_CALLS("select count(distinct callId) from JobAidContentMeasure", "JobAid Calls"),

    FIND_TODAY_JOB_AID_CALLS(
            " select count(distinct jacm.callId) from JobAidContentMeasure jacm, TimeDimension td" +
                    " where td.id = jacm.timeDimension.id" +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "JobAid Calls Today"),

    FIND_TOTAL_COURSE_CALLS("select count(distinct callId) from CourseItemMeasure", "CertificateCourse Calls"),
    
    FIND_TODAY_COURSE_CALLS(
            " select count(distinct cim.callId) from CourseItemMeasure cim, TimeDimension td " +
                    " where td.id = cim.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "CertificateCourse Calls Today"),

    FIND_TOTAL_SMS_SENT("select count(*) from SMSSentMeasure where smsSent = true", "SMS Sent"),

    FIND_TODAY_SMS_SENT(
            "select count(*) from SMSSentMeasure ssm, TimeDimension td " +
                    " where ssm.smsSent = true " +
                    " and td.id = ssm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "SMS Sent Today");

    private String query;
    private String description;

    private DiagnosticQuery(String query, String description) {
        this.query = query;
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public String getQuery(DateTime today) {
        return (String.format(query, today.getDayOfYear(), today.getMonthOfYear(), today.getYear()));
    }

    public String title() {
        return description;
    }
}
