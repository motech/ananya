package org.motechproject.ananya.support.diagnostics.base;

import org.joda.time.DateTime;

public enum DiagnosticQuery {

    FIND_TOTAL_FLWS("select count(*) from FrontLineWorkerDimension", "Total number of FLWs"),

    FIND_FLWS_REG_TODAY(
            " select count(*) from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td" +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id" +
                    " and td.day =  %s and td.month = %s and td.year = %s",
            "Total number of FLWs registered today"),

    FIND_TOTAL_FLWS_BY_STATUS("select count(*),status from FrontLineWorkerDimension group by status", "Total number of FLWs by status"),

    FIND_FLWS_BY_STATUS_TODAY(
            " select count(*),flwd.status from FrontLineWorkerDimension flwd, RegistrationMeasure rm, TimeDimension td " +
                    " where flwd.id = rm.frontLineWorkerDimension.id" +
                    " and td.id = rm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s" +
                    " group by flwd.status",
            "Total number of FLWs registered today by status"),
    FIND_TOTAL_JOB_AID_CALLS("select count(distinct callId) from JobAidContentMeasure", "Total number of jobaid calls"),
    
    FIND_TODAY_JOB_AID_CALLS(
            " select count(distinct jacm.callId) from JobAidContentMeasure jacm, TimeDimension td" +
                    " where td.id = jacm.timeDimension.id" +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "Total number of jobaid calls today"),

    FIND_TOTAL_CCOURSE_CALLS("select count(distinct callId) from CourseItemMeasure", "Total number of certificate course calls"),

    FIND_TODAY_CCOURSE_CALLS(
            " select count(distinct cim.callId) from CourseItemMeasure cim, TimeDimension td " +
                    " where td.id = cim.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "Total number of certificate course calls today"),

    FIND_TOTAL_SMS_SENT("select count(*) from SMSSentMeasure where smsSent = true", "Total number of SMS"),

    FIND_TODAY_SMS_SENT(
            "select count(*) from SMSSentMeasure ssm, TimeDimension td " +
                    " where ssm.smsSent = true " +
                    " and td.id = ssm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "Total number of SMS today");

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

    public String getDescription() {
        return description;
    }
}
