package org.motechproject.ananya.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.response.CallType;
import org.motechproject.ananya.response.FLWCallDetail;
import org.motechproject.ananya.response.FrontLineWorkerUsageResponse;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerUsageResponseMapperTest {
    @Test
    public void shouldMapToFrontLineWorkerUsageResponse() {
        final DateTime startTime = DateTime.now();
        final DateTime endTime = startTime.plusMinutes(2);
        String dateTimeFormat = "dd-MM-yyyy hh:mm:ss";
        FrontLineWorker frontLineWorker = new FrontLineWorker("msisdn", "operator", "circle");
        Integer lessonIndex = 11;
        Integer chapterIndex = 12;
        frontLineWorker.addBookMark(new BookMark("some", chapterIndex, lessonIndex));
        final int month = 12;
        final int year = 2012;
        ArrayList<CallUsageDetails> jobAidCallUsageDetails = new ArrayList<CallUsageDetails>() {{
            add(new CallUsageDetails(123L, 324L, year, month));
        }};
        ArrayList<CallDurationMeasure> jobAidCallDurationMeasureList = new ArrayList<CallDurationMeasure>() {{
            add(new CallDurationMeasure(null, null, null, null, null, 123, startTime, endTime, "CALL"));
        }};
        ArrayList<CallDurationMeasure> certificateCourseCallDurationMeasureList = new ArrayList<CallDurationMeasure>() {{
            add(new CallDurationMeasure(null, null, null, null, null, 1324, startTime, endTime, "CALL"));
        }};
        CallDetailsResponse callDetails = new CallDetailsResponse(jobAidCallUsageDetails, jobAidCallDurationMeasureList, certificateCourseCallDurationMeasureList);
        Location location = Location.getDefaultLocation();
        SMSReference smsReference = new SMSReference();
        String smsReferenceNumber = "1231231231";
        smsReference.add(smsReferenceNumber, 1);

        FrontLineWorkerUsageResponse frontLineWorkerUsageResponse = new FrontLineWorkerUsageResponseMapper().mapFrom(frontLineWorker, location, callDetails, smsReference);

        assertEquals(frontLineWorkerUsageResponse.getName(), frontLineWorker.getName());
        assertEquals(frontLineWorkerUsageResponse.getDesignation(), frontLineWorker.getDesignation());
        assertEquals(frontLineWorkerUsageResponse.getRegistrationStatus(), frontLineWorker.getStatus().name());
        assertEquals(frontLineWorkerUsageResponse.getLocation().getBlock(), location.getBlock());
        assertEquals(frontLineWorkerUsageResponse.getLocation().getDistrict(), location.getDistrict());
        assertEquals(frontLineWorkerUsageResponse.getLocation().getPanchayat(), location.getPanchayat());
        assertEquals((int) frontLineWorkerUsageResponse.getUsageDetails().get(0).getMonth(), month);
        assertEquals((int) frontLineWorkerUsageResponse.getUsageDetails().get(0).getYear(), year);

        long jaUsageInMinutes = Math.round(jobAidCallUsageDetails.get(0).getJobAidDurationInSec() / 60);
        long ccUsageInMinutes = Math.round(jobAidCallUsageDetails.get(0).getCertificateCourseDurationInSec() / 60);
        assertEquals(frontLineWorkerUsageResponse.getUsageDetails().get(0).getMobileKunji(), Long.valueOf(jaUsageInMinutes));
        assertEquals(frontLineWorkerUsageResponse.getUsageDetails().get(0).getMobileAcademy(), Long.valueOf(ccUsageInMinutes));
        assertEquals(2, frontLineWorkerUsageResponse.getCallDetails().size());

        FLWCallDetail jobAidFlwCallDetails = frontLineWorkerUsageResponse.getCallDetails().get(0);
        assertEquals((Integer) Math.round(jobAidCallDurationMeasureList.get(0).getDuration() / 60), jobAidFlwCallDetails.getMinutes());
        assertEquals(CallType.MOBILE_KUNJI, jobAidFlwCallDetails.getCallType());
        assertEquals(endTime.toString(dateTimeFormat), jobAidFlwCallDetails.getEndTime());
        assertEquals(startTime.toString(dateTimeFormat), jobAidFlwCallDetails.getStartTime());

        FLWCallDetail ccFlwCallDetails = frontLineWorkerUsageResponse.getCallDetails().get(1);
        assertEquals((Integer) Math.round(certificateCourseCallDurationMeasureList.get(0).getDuration() / 60), ccFlwCallDetails.getMinutes());
        assertEquals(CallType.MOBILE_ACADEMY, ccFlwCallDetails.getCallType());
        assertEquals(endTime.toString(dateTimeFormat), ccFlwCallDetails.getEndTime());
        assertEquals(startTime.toString(dateTimeFormat), ccFlwCallDetails.getStartTime());

        assertEquals(chapterIndex, frontLineWorkerUsageResponse.getBookmark().getChapter());
        assertEquals(lessonIndex, frontLineWorkerUsageResponse.getBookmark().getLesson());

        assertEquals(1, frontLineWorkerUsageResponse.getSmsReferenceNumbers().size());
        assertEquals(smsReferenceNumber, frontLineWorkerUsageResponse.getSmsReferenceNumbers().get(0));
    }
}
