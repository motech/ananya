package org.motechproject.ananya.response;

public class AnanyaMonitorResponse {

    private int totalCallsJobAid;

    private int todayCallsJobAid;

    private int totalCallsCertificateCourse;

    private int todayCallsCertificateCourse;

    private int totalSMSCertificateCourse;

    private int todaySMSCertificateSMS;

    private int registeredFLWCount;

    private int partiallyRegisteredFLWCount;

    private int unregisteredFLWCount;

    public AnanyaMonitorResponse(int totalCallsJobAid, int todayCallsJobAid, int totalCallsCertificateCourse,
                                 int todayCallsCertificateCourse, int totalSMSCertificateCourse,
                                 int todaySMSCertificateSMS, int registeredFLWCount, int partiallyRegisteredFLWCount,
                                 int unregisteredFLWCount) {
        this.totalCallsJobAid = totalCallsJobAid;
        this.todayCallsJobAid = todayCallsJobAid;
        this.totalCallsCertificateCourse = totalCallsCertificateCourse;
        this.todayCallsCertificateCourse = todayCallsCertificateCourse;
        this.totalSMSCertificateCourse = totalSMSCertificateCourse;
        this.todaySMSCertificateSMS = todaySMSCertificateSMS;
        this.registeredFLWCount = registeredFLWCount;
        this.partiallyRegisteredFLWCount = partiallyRegisteredFLWCount;
        this.unregisteredFLWCount = unregisteredFLWCount;
    }

    public int getTotalCallsJobAid() {
        return totalCallsJobAid;
    }

    public int getTodayCallsJobAid() {
        return todayCallsJobAid;
    }

    public int getTotalCallsCertificateCourse() {
        return totalCallsCertificateCourse;
    }

    public int getTodayCallsCertificateCourse() {
        return todayCallsCertificateCourse;
    }

    public int getTotalSMSCertificateCourse() {
        return totalSMSCertificateCourse;
    }

    public int getTodaySMSCertificateSMS() {
        return todaySMSCertificateSMS;
    }

    public int getRegisteredFLWCount() {
        return registeredFLWCount;
    }

    public int getPartiallyRegisteredFLWCount() {
        return partiallyRegisteredFLWCount;
    }

    public int getUnregisteredFLWCount() {
        return unregisteredFLWCount;
    }
}
