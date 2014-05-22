package org.motechproject.ananya.support.admin.domain;

public enum AdminQuery {
    ACADEMY_CALLS(
            "select cim.callId as callId, cim.timestamp as timeStamp, cid.name as contentName, cid.fileName as contentFileName" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   CourseItemMeasure cim," +
                    "   CourseItemDimension cid" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and cim.frontLineWorkerDimension.id = flwd.id" +
                    "   and cid.id = cim.courseItemDimension.id" +
                    " order by timeStamp desc",
            "Academy Calls"
    ),

    KUNJI_CALLS(
            "select jacm.callId as callId, jacm.timestamp as timeStamp, jacd.name as contentName, jacd.fileName as contentFileName" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   JobAidContentMeasure jacm," +
                    "   JobAidContentDimension jacd" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and jacm.frontLineWorkerDimension.id = flwd.id" +
                    "   and jacd.id = jacm.jobAidContentDimension.id" +
                    " order by timeStamp desc",
            "Kunji Calls"
    ),
    CALL_DETAILS(
            "select cdm.callId as callId, cdm.startTime, cdm.endTime, cdm.duration, cdm.calledNumber, cdm.type" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   CallDurationMeasure cdm" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and cdm.frontLineWorkerDimension.id = flwd.id" +
                    " order by cdm.startTime desc",
            "Calls Details"
    ),
    CALLER_DETAIL(
            "select flwd.msisdn, flwd.name" +
                    " from" +
                    "   FrontLineWorkerDimension flwd" +
                    " where" +
                    "   flwd.msisdn = %s",
            "Caller Detail"
    ),
    ACADEMY_CALLS_WITH_LANGUAGE(
            "select cim.callId as callId, cim.timestamp as timeStamp, cid.name as contentName, cidd.fileName as contentFileName, cim.languageDimension.name as contentLanguage" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   CourseItemMeasure cim," +
                    "   CourseItemDimension cid," +
                    "   CourseItemDetailsDimension cidd" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and cim.frontLineWorkerDimension.id = flwd.id" +
                    "   and cid.id = cim.courseItemDimension.id" +
                    "   and cidd.contentId = cim.courseItemDimension.contentId" +
                    "   and cidd.languageId = cim.languageDimension.id" +
                    " order by timeStamp desc",
            "Academy Calls"
    ),

    KUNJI_CALLS_WITH_LANGUAGE(
            "select jacm.callId as callId, jacm.timestamp as timeStamp, jacd.name as contentName, jacdd.fileName as contentFileName, jacm.languageDimension.name as contentLanguage" +
                    " from" +
                    "   FrontLineWorkerDimension flwd," +
                    "   JobAidContentMeasure jacm," +
                    "   JobAidContentDimension jacd," +
                    "   JobAidContentDetailsDimension jacdd" +
                    " where" +
                    "   flwd.msisdn = %s" +
                    "   and jacm.frontLineWorkerDimension.id = flwd.id" +
                    "   and jacd.id = jacm.jobAidContentDimension.id" +
                    "   and jacdd.contentId = jacm.jobAidContentDimension.contentId" +
                    "   and jacdd.languageId = jacm.languageDimension.id" +
                    " order by timeStamp desc",
            "Kunji Calls"
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
