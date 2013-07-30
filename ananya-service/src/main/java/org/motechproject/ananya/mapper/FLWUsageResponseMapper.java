package org.motechproject.ananya.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.response.*;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.motechproject.ananya.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

@Component
public class FLWUsageResponseMapper {

    public FLWNighttimeCallsResponse mapNighttimeCallsResponse(List<JobAidCallDetails> jobAidCallDetailsList) {
        return new FLWNighttimeCallsResponse(mapCallDurations(jobAidCallDetailsList));
    }

    public FLWUsageResponse mapUsageResponse(FrontLineWorker frontLineWorker, Location location, CallDetailsResponse callDetails, SMSReference smsReference) {
        LocationResponse locationResponse = location == null ? null : new LocationResponse(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat());
        List<FLWUsageDetail> flwUsageDetailList = mapUsageDetails(callDetails.getCallUsageDetailsList());
        List<FLWCallDetail> flwCallDetails = mapFrom(callDetails);
        BookMark bookmark = frontLineWorker.bookMark();
        FLWBookmark flwBookmark = bookmark.isEmptyBookmark() ? null : new FLWBookmark(bookmark.getChapterIndex(), bookmark.getLessonIndex());
        String verificationStatus = frontLineWorker.getVerificationStatus() == null ? "" : frontLineWorker.getVerificationStatus().name();
        String alternateContactNumber = trimToEmpty(frontLineWorker.getAlternateContactNumber());
        return new FLWUsageResponse(
                frontLineWorker.getFlwId().toString(),
                frontLineWorker.getName(),
                frontLineWorker.designationName(),
                verificationStatus,
                frontLineWorker.getStatus().name(),
                alternateContactNumber,
                locationResponse,
                flwUsageDetailList,
                flwCallDetails,
                flwBookmark,
                mapFrom(smsReference)
        );
    }

    private List<FLWCallDetail> mapFrom(CallDetailsResponse callDetails) {
        List<FLWCallDetail> flwCallDetails = new ArrayList<>();
        flwCallDetails.addAll(mapCallDetails(callDetails.getRecentJobAidCallDetailsList(), CallType.MOBILE_KUNJI));
        flwCallDetails.addAll(mapCallDetails(callDetails.getRecentCertificateCourseCallDetailsList(), CallType.MOBILE_ACADEMY));
        return flwCallDetails;
    }

    private List<String> mapFrom(SMSReference smsReference) {
        if (smsReference == null)
            return null;
        Map<Integer, String> referenceNumbers = smsReference.getReferenceNumbers();
        List<String> smsReferenceNumbers = new ArrayList<>();
        for (String referenceNumber : referenceNumbers.values()) {
            smsReferenceNumbers.add(referenceNumber);
        }
        return smsReferenceNumbers;
    }

    private List<FLWUsageDetail> mapUsageDetails(List<CallUsageDetails> callUsageDetailsList) {
        return (List<FLWUsageDetail>) CollectionUtils.collect(callUsageDetailsList, new Transformer() {
            @Override
            public Object transform(Object input) {
                CallUsageDetails callUsageDetails = (CallUsageDetails) input;
                return new FLWUsageDetail(callUsageDetails.getYear(), callUsageDetails.getMonth(), callUsageDetails.getJobAidDurationInPulse(), callUsageDetails.getCertificateCourseDurationInPulse());
            }
        });
    }

    private Collection<FLWCallDetail> mapCallDetails(List<CallDurationMeasure> callDurationMeasures, final CallType callType) {
        return CollectionUtils.collect(callDurationMeasures, new Transformer() {
            @Override
            public Object transform(Object input) {
                CallDurationMeasure callDurationMeasure = (CallDurationMeasure) input;
                return new FLWCallDetail(callType, new DateTime(callDurationMeasure.getStartTime()).toString(DateUtils.DATE_TIME_FORMAT), new DateTime(callDurationMeasure.getEndTime()).toString(DateUtils.DATE_TIME_FORMAT), Math.round(callDurationMeasure.getDurationInPulse()));
            }
        });
    }

    private List<FLWCallDuration> mapCallDurations(List<JobAidCallDetails> jobAidCallDetailsList) {
        return (List<FLWCallDuration>) CollectionUtils.collect(jobAidCallDetailsList, new Transformer() {
            @Override
            public Object transform(Object input) {
                JobAidCallDetails jobAidCallDetails = (JobAidCallDetails) input;
                return new FLWCallDuration(jobAidCallDetails.getStartTime().toString(DateUtils.DATE_TIME_FORMAT), jobAidCallDetails.getEndTime().toString(DateUtils.DATE_TIME_FORMAT), jobAidCallDetails.getDurationInPulse());
            }
        });
    }
}
