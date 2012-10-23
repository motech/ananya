package org.motechproject.ananya.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.response.*;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class FrontLineWorkerUsageResponseMapper {

    public final String DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm:ss";

    public FrontLineWorkerUsageResponse mapFrom(FrontLineWorker frontLineWorker, Location location, CallDetailsResponse callDetails, SMSReference smsReference) {
        LocationResponse locationResponse = new LocationResponse(location.getDistrict(), location.getBlock(), location.getPanchayat());
        List<FLWUsageDetail> flwUsageDetailList = mapFrom(callDetails.getCallUsageDetailsList());
        List<FLWCallDetail> flwCallDetails = mapFrom(callDetails);
        BookMark bookMark = frontLineWorker.bookMark();

        return new FrontLineWorkerUsageResponse(
                frontLineWorker.getName(),
                frontLineWorker.designationName(),
                frontLineWorker.getStatus().name(),
                locationResponse,
                flwUsageDetailList,
                flwCallDetails,
                new FLWBookmark(bookMark.getChapterIndex(), bookMark.getLessonIndex()),
                mapFrom(smsReference)
        );
    }

    private List<FLWCallDetail> mapFrom(CallDetailsResponse callDetails) {
        List<FLWCallDetail> flwCallDetails = new ArrayList<>();
        flwCallDetails.addAll(mapFrom(callDetails.getRecentJobAidCallDetailsList(), CallType.MOBILE_KUNJI));
        flwCallDetails.addAll(mapFrom(callDetails.getRecentCertificateCourseCallDetailsList(), CallType.MOBILE_ACADEMY));
        return flwCallDetails;
    }

    private List<String> mapFrom(SMSReference smsReference) {
        Map<Integer, String> referenceNumbers = smsReference.getReferenceNumbers();
        List<String> smsReferenceNumbers = new ArrayList<>();
        for (String referenceNumber : referenceNumbers.values()) {
            smsReferenceNumbers.add(referenceNumber);
        }
        return smsReferenceNumbers;
    }

    private List<FLWUsageDetail> mapFrom(List<CallUsageDetails> callUsageDetailsList) {
        return (List<FLWUsageDetail>) CollectionUtils.collect(callUsageDetailsList, new Transformer() {
            @Override
            public Object transform(Object input) {
                CallUsageDetails callUsageDetails = (CallUsageDetails) input;
                return new FLWUsageDetail(callUsageDetails.getYear(), callUsageDetails.getMonth(), convertToMinutes(callUsageDetails.getJobAidDurationInSec()), convertToMinutes(callUsageDetails.getCertificateCourseDurationInSec()));
            }
        });
    }

    private Collection<FLWCallDetail> mapFrom(List<CallDurationMeasure> callDurationMeasures, final CallType callType) {
        return CollectionUtils.collect(callDurationMeasures, new Transformer() {
            @Override
            public Object transform(Object input) {
                CallDurationMeasure callDurationMeasure = (CallDurationMeasure) input;
                return new FLWCallDetail(callType, new DateTime(callDurationMeasure.getStartTime()).toString(DATE_TIME_FORMAT), new DateTime(callDurationMeasure.getEndTime()).toString(DATE_TIME_FORMAT), Math.round(callDurationMeasure.getDuration() / 60));
            }
        });
    }

    private Long convertToMinutes(Long seconds) {
        return Math.round(seconds / 60.0);
    }
}
