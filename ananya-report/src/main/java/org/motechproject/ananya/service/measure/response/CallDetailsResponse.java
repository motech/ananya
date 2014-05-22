package org.motechproject.ananya.service.measure.response;

import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;

import java.util.List;

public class CallDetailsResponse {
    List<CallUsageDetails> callUsageDetailsList;
    private final List<CallDurationMeasure> recentJobAidCallDetailsList;
    private final List<CallDurationMeasure> recentCertificateCourseCallDetailsList;

    public CallDetailsResponse(List<CallUsageDetails> callUsageDetailsList,
                               List<CallDurationMeasure> recentJobAidCallDetailsList,
                               List<CallDurationMeasure> recentCertificateCourseCallDetailsList) {
        this.callUsageDetailsList = callUsageDetailsList;
        this.recentJobAidCallDetailsList = recentJobAidCallDetailsList;
        this.recentCertificateCourseCallDetailsList = recentCertificateCourseCallDetailsList;
    }

    public List<CallUsageDetails> getCallUsageDetailsList() {
        return callUsageDetailsList;
    }

    public List<CallDurationMeasure> getRecentJobAidCallDetailsList() {
        return recentJobAidCallDetailsList;
    }

    public List<CallDurationMeasure> getRecentCertificateCourseCallDetailsList() {
        return recentCertificateCourseCallDetailsList;
    }
}
