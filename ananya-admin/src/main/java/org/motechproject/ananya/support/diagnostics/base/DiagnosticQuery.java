package org.motechproject.ananya.support.diagnostics.base;

import org.joda.time.DateTime;

public enum DiagnosticQuery {

    FIND_TOTAL_FLWS("select count(*) from FrontLineWorkerDimension", "FLWs"),

    FIND_TOTAL_FLWS_BY_STATUS("select count(*),status from FrontLineWorkerDimension group by status", "FLWs by Status"),

    FIND_TODAY_FLWS(
            " select count(distinct cdm.frontLineWorkerDimension.id) from CallDurationMeasure cdm, TimeDimension td" +
                    " where td.id = cdm.timeDimension.id" +
                    " and cdm.type='CALL'" +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "FLWs Called Today"),

    FIND_TODAY_FLWS_BY_STATUS(
            "select count(distinct cdm.frontLineWorkerDimension.id), cdm.frontLineWorkerDimension.status from CallDurationMeasure cdm, TimeDimension td" +
                    " where td.id = cdm.timeDimension.id" +
                    " and cdm.type='CALL'" +
                    " and td.day = %s and td.month = %s and td.year = %s" +
                    " group by cdm.frontLineWorkerDimension.status",
            "FLWs Called Today By Status"),

    FIND_TOTAL_JOB_AID_CALLS("select count(distinct callId) from CallDurationMeasure cdm where cdm.type='JOBAID'", "JobAid Calls"),

    FIND_TODAY_JOB_AID_CALLS(
            " select count(distinct cdm.callId) from CallDurationMeasure cdm, TimeDimension td" +
                    " where td.id = cdm.timeDimension.id" +
                    " and cdm.type='JOBAID'" +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "JobAid Calls Today"),

    FIND_TOTAL_COURSE_CALLS("select count(distinct callId) from CallDurationMeasure cdm where cdm.type='CERTIFICATECOURSE'", "CertificateCourse Calls"),

    FIND_TODAY_COURSE_CALLS(
            " select count(distinct cdm.callId) from CallDurationMeasure cdm , TimeDimension td " +
                    " where td.id = cdm.timeDimension.id " +
                    " and cdm.type='CERTIFICATECOURSE'" +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "CertificateCourse Calls Today"),

    FIND_TOTAL_SMS_SENT("select count(*) from SMSSentMeasure where smsSent = true", "SMS Sent"),

    FIND_TODAY_SMS_SENT(
            "select count(*) from SMSSentMeasure ssm, TimeDimension td " +
                    " where ssm.smsSent = true " +
                    " and td.id = ssm.timeDimension.id " +
                    " and td.day = %s and td.month = %s and td.year = %s",
            "SMS Sent Today"),

    /** New query for state*/
    FIND_TOTAL_FLWS_GROUP_BY_STATE("select count(rem), rem.locationDimension.state from RegistrationMeasure rem group by rem.locationDimension.state order by rem.locationDimension.state", "FLWs"),

    FIND_TOTAL_FLWS_BY_STATUS_GROUP_BY_STATE("select count(rem), rem.frontLineWorkerDimension.status, rem.locationDimension.state from RegistrationMeasure rem group by rem.locationDimension.state, rem.frontLineWorkerDimension.status order by rem.locationDimension.state", "FLWs by Status"),

    FIND_TODAY_FLWS_GROUP_BY_STATE(
            " select count(distinct cdm.frontLineWorkerDimension.id), cdm.locationDimension.state from CallDurationMeasure cdm, TimeDimension td" +
                    " where td.id = cdm.timeDimension.id" +
                    " and cdm.type='CALL'" +
                    " and td.day = %s and td.month = %s and td.year = %s group by cdm.locationDimension.state order by cdm.locationDimension.state",
            "FLWs Called Today"),

    FIND_TODAY_FLWS_BY_STATUS_GROUP_BY_STATE(
            "select count(distinct cdm.frontLineWorkerDimension.id), cdm.frontLineWorkerDimension.status, cdm.locationDimension.state from CallDurationMeasure cdm, TimeDimension td" +
                    " where td.id = cdm.timeDimension.id" +
                    " and cdm.type='CALL'" +
                    " and td.day = %s and td.month = %s and td.year = %s" +
                    " group by cdm.frontLineWorkerDimension.status, cdm.locationDimension.state order by cdm.locationDimension.state",
            "FLWs Called Today By Status"),

   FIND_TOTAL_JOB_AID_CALLS_GROUP_BY_STATE("select count(distinct callId), cdm.locationDimension.state from CallDurationMeasure cdm where cdm.type='JOBAID' group by cdm.locationDimension.state order by cdm.locationDimension.state", "JobAid Calls"),

   FIND_TODAY_JOB_AID_CALLS_GROUP_BY_STATE(
                    " select count(distinct cdm.callId), cdm.locationDimension.state from CallDurationMeasure cdm, TimeDimension td" +
                            " where td.id = cdm.timeDimension.id" +
                            " and cdm.type='JOBAID'" +
                            " and td.day = %s and td.month = %s and td.year = %s " +
                            "group by cdm.locationDimension.state order by cdm.locationDimension.state",
                    "JobAid Calls Today"),
     
   FIND_TOTAL_COURSE_CALLS_GROUP_BY_STATE("select count(distinct callId), cdm.locationDimension.state from CallDurationMeasure cdm where cdm.type='CERTIFICATECOURSE'"+
		   	" group by cdm.locationDimension.state order by cdm.locationDimension.state", "CertificateCourse Calls"),

   FIND_TODAY_COURSE_CALLS_GROUP_BY_STATE(" select count(distinct cdm.callId), cdm.locationDimension.state from CallDurationMeasure cdm , TimeDimension td " +
                                    " where td.id = cdm.timeDimension.id " +
                                    " and cdm.type='CERTIFICATECOURSE'" +
                                    " and td.day = %s and td.month = %s and td.year = %s"+
                                    " group by cdm.locationDimension.state order by cdm.locationDimension.state",
                            "CertificateCourse Calls Today"),
                            
   FIND_TOTAL_SMS_SENT_GROUP_BY_STATE("select count(*), locationDimension.state from SMSSentMeasure where smsSent = true"+
		   " group by locationDimension.state order by locationDimension.state", "SMS Sent"),

   FIND_TODAY_SMS_SENT_GROUP_BY_STATE("select count(*), ssm.locationDimension.state from SMSSentMeasure ssm, TimeDimension td " +
                                            " where ssm.smsSent = true " +
                                            " and td.id = ssm.timeDimension.id " +
                                            " and td.day = %s and td.month = %s and td.year = %s"+
                                            " group by ssm.locationDimension.state order by ssm.locationDimension.state",
                                    "SMS Sent Today")                
    ;
    
    private String query;
    private String description;

    private DiagnosticQuery(String query, String description) {
        this.query = query;
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public String getQuery(DateTime date) {
        return (String.format(query, date.getDayOfYear(), date.getMonthOfYear(), date.getYear()));
    }

    public String getQueryByState(String state) {
        return (String.format(query, state));
    }

    public String getQueryByState(DateTime date, String state) {
        return (String.format(query, date.getDayOfYear(), date.getMonthOfYear(), date.getYear(), state));
    }

    public String title() {
        return description;
    }
}
