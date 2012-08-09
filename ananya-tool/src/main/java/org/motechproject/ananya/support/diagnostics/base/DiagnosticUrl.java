package org.motechproject.ananya.support.diagnostics.base;

public enum DiagnosticUrl {

    FIND_TOTAL_FRONT_LINE_WORKERS("http://%s:%s/ananya/_design/FrontLineWorker/_view/by_msisdn?limit=0","Total number of FrontlineWorkers"),
    FIND_TOTAL_LOCATIONS("http://%s:%s/ananya/_design/Location/_view/by_externalId?limit=0","Total number of Locations"),
    FIND_TOTAL_OPERATORS("http://%s:%s/ananya/_design/Operator/_view/by_name?limit=0","Total number of Operators"),
    FIND_TOTAL_AUDIO_TRACKER_LOGS("http://%s:%s/ananya/_design/AudioTrackerLog/_view/by_callId?limit=0","Total number of AudioTrackerLogs"),
    FIND_TOTAL_CALL_LOGS("http://%s:%s/ananya/_design/CallLog/_view/by_callId?limit=0","Total number of CallLogs"),
    FIND_TOTAL_CERTIFICATE_COURSE_LOGS("http://%s:%s/ananya/_design/CertificationCourseLog/_view/by_callId?limit=0","Total number of CertificateCourseLogs"),
    FIND_TOTAL_SMS_LOGS("http://%s:%s/ananya/_design/SMSLog/_view/by_callId?limit=0","Total number of SMS Logs"),
    FIND_TOTAL_SMS_REFERENCES("http://%s:%s/ananya/_design/SMSReference/_view/by_msisdn?limit=0","Total number of SMS references");

    private String url;
    private String description;

    DiagnosticUrl(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public String getFor(String server, String port) {
        return String.format(url, server, port);
    }

    public String description() {
        return description;
    }
}
