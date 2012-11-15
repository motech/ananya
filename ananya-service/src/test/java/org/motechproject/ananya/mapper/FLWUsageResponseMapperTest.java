package org.motechproject.ananya.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.response.*;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.motechproject.ananya.utils.DateUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FLWUsageResponseMapperTest {
    @Test
    public void shouldMapToFLWUsageResponse() {
        final DateTime startTime = DateTime.now();
        final DateTime endTime = startTime.plusMinutes(2);
        String dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
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
            add(new CallDurationMeasure(new FrontLineWorkerDimension(), null, null, null, null, 123, startTime, endTime, "CALL", 12));
        }};
        ArrayList<CallDurationMeasure> certificateCourseCallDurationMeasureList = new ArrayList<CallDurationMeasure>() {{
            add(new CallDurationMeasure(new FrontLineWorkerDimension(), null, null, null, null, 1324, startTime, endTime, "CALL", 13));
        }};
        CallDetailsResponse callDetails = new CallDetailsResponse(jobAidCallUsageDetails, jobAidCallDurationMeasureList, certificateCourseCallDurationMeasureList);
        Location location = Location.getDefaultLocation();
        SMSReference smsReference = new SMSReference();
        String smsReferenceNumber = "1231231231";
        smsReference.add(smsReferenceNumber, 1);

        FLWUsageResponse frontLineWorkerUsageResponse = new FLWUsageResponseMapper().mapUsageResponse(frontLineWorker, location, callDetails, smsReference);

        assertEquals(frontLineWorker.getName(), frontLineWorkerUsageResponse.getName());
        assertEquals(frontLineWorker.getDesignation(), frontLineWorkerUsageResponse.getDesignation());
        assertEquals(frontLineWorker.getStatus().name(), frontLineWorkerUsageResponse.getRegistrationStatus());
        assertEquals(location.getBlock(), frontLineWorkerUsageResponse.getLocation().getBlock());
        assertEquals(location.getDistrict(), frontLineWorkerUsageResponse.getLocation().getDistrict());
        assertEquals(location.getPanchayat(), frontLineWorkerUsageResponse.getLocation().getPanchayat());
        assertEquals(month, (int) frontLineWorkerUsageResponse.getUsageDetails().get(0).getMonth());
        assertEquals(year, (int) frontLineWorkerUsageResponse.getUsageDetails().get(0).getYear());

        assertEquals(Long.valueOf(123), frontLineWorkerUsageResponse.getUsageDetails().get(0).getMobileKunji());
        assertEquals(Long.valueOf(324), frontLineWorkerUsageResponse.getUsageDetails().get(0).getMobileAcademy());
        assertEquals(2, frontLineWorkerUsageResponse.getCallDetails().size());

        FLWCallDetail jobAidFlwCallDetails = frontLineWorkerUsageResponse.getCallDetails().get(0);
        assertEquals(Integer.valueOf(12), jobAidFlwCallDetails.getPulse());
        assertEquals(CallType.MOBILE_KUNJI, jobAidFlwCallDetails.getCallType());
        assertEquals(endTime.toString(dateTimeFormat), jobAidFlwCallDetails.getEndTime());
        assertEquals(startTime.toString(dateTimeFormat), jobAidFlwCallDetails.getStartTime());

        FLWCallDetail ccFlwCallDetails = frontLineWorkerUsageResponse.getCallDetails().get(1);
        assertEquals(Integer.valueOf(13), ccFlwCallDetails.getPulse());
        assertEquals(CallType.MOBILE_ACADEMY, ccFlwCallDetails.getCallType());
        assertEquals(endTime.toString(dateTimeFormat), ccFlwCallDetails.getEndTime());
        assertEquals(startTime.toString(dateTimeFormat), ccFlwCallDetails.getStartTime());

        assertEquals(chapterIndex, frontLineWorkerUsageResponse.getBookmark().getChapter());
        assertEquals(lessonIndex, frontLineWorkerUsageResponse.getBookmark().getLesson());

        assertEquals(1, frontLineWorkerUsageResponse.getSmsReferenceNumbers().size());
        assertEquals(smsReferenceNumber, frontLineWorkerUsageResponse.getSmsReferenceNumbers().get(0));
    }

    @Test
    public void shouldMapToFLWNighttimeCallsResponse() {
        final DateTime startDate = DateTime.now();

        ArrayList<JobAidCallDetails> jobAidCallDetailsList = new ArrayList<JobAidCallDetails>() {{
            add(new JobAidCallDetails(startDate, startDate.plusMinutes(1), 10));
            add(new JobAidCallDetails(startDate.plusMinutes(4), startDate.plusMinutes(5), 15));
        }};

        FLWNighttimeCallsResponse flwNighttimeCallsResponse = new FLWUsageResponseMapper().mapNighttimeCallsResponse(jobAidCallDetailsList);
        List<FLWCallDuration> callDetails = flwNighttimeCallsResponse.getCallDurations();
        FLWCallDuration exptectdDuration1 = new FLWCallDuration(DateUtils.formatDateTime(startDate), DateUtils.formatDateTime(startDate.plusMinutes(1)), 10);
        FLWCallDuration expectedDuration2 = new FLWCallDuration(DateUtils.formatDateTime(startDate.plusMinutes(4)), DateUtils.formatDateTime(startDate.plusMinutes(5)), 15);
        assertEquals(exptectdDuration1, callDetails.get(0));
        assertEquals(expectedDuration2, callDetails.get(1));
    }

    @Test
    public void shouldHandleNullLocation() {
        FLWUsageResponse flwUsageResponse = new FLWUsageResponseMapper().mapUsageResponse(new FrontLineWorker(), null, new CallDetailsResponse(new ArrayList<CallUsageDetails>(), new ArrayList<CallDurationMeasure>(), new ArrayList<CallDurationMeasure>()), new SMSReference());

        assertNull(flwUsageResponse.getLocation());
    }

    @Test
    public void shouldHandleNullSMSReferenceNumber() {
        FLWUsageResponse flwUsageResponse = new FLWUsageResponseMapper().mapUsageResponse(new FrontLineWorker(), null, new CallDetailsResponse(new ArrayList<CallUsageDetails>(), new ArrayList<CallDurationMeasure>(), new ArrayList<CallDurationMeasure>()), null);

        assertNull(flwUsageResponse.getLocation());
    }

    @Test
    public void shouldNotHaveEmptyBookmark() {
        FLWUsageResponse flwUsageResponse = new FLWUsageResponseMapper().mapUsageResponse(new FrontLineWorker(), null, new CallDetailsResponse(new ArrayList<CallUsageDetails>(), new ArrayList<CallDurationMeasure>(), new ArrayList<CallDurationMeasure>()), null);

        assertNull(flwUsageResponse.getBookmark());
    }

    public static String toJson(Object objectToSerialize) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, objectToSerialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}
