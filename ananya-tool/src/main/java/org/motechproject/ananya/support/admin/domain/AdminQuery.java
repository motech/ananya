package org.motechproject.ananya.support.admin.domain;

public enum AdminQuery {
    ACADEMY_CALLS(
            "select flwd.name as name, flwd.msisdn as msidn, cim.callId as callId, cim.timestamp as timeStamp, cid.name as contentName, cid.fileName as contentFileName" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   CourseItemMeasure cim," +
                    "   CourseItemDimension cid" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and cim.frontLineWorkerDimension.id = flwd.id" +
                    "   and cid.id = cim.courseItemDimension.id" +
                    " order by timeStamp",
            "Academy Calls"
    ),

    KUNJI_CALLS(
            "select flwd.name as name, flwd.msisdn as msidn, jacm.callId as callId, jacm.timestamp as timeStamp, jacd.name as contentName, jacd.fileName as contentFileName" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   JobAidContentMeasure jacm," +
                    "   JobAidContentDimension jacd" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and jacm.frontLineWorkerDimension.id = flwd.id" +
                    "   and jacd.id = jacm.jobAidContentDimension.id" +
                    " order by timeStamp",
            "Kunji Calls"
    ),
    CALL_DETAILS(
            "select flwd.name as name, flwd.msisdn as msidn, cdm.callId as callId, cdm.startTime, cdm.endTime, cdm.duration, cdm.calledNumber, cdm.type" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   CallDurationMeasure cdm" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and cdm.frontLineWorkerDimension.id = flwd.id" +
                    " order by cdm.startTime",
            "Calls Details"
    );
    private String query;
    private String description;

    private AdminQuery(String query, String description) {
        this.query = query;
        this.description = description;
    }

    public String getQuery(String... params) {
        return String.format(query, params);
    }

    public String getDescription() {
        return description;
    }
}
