package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.JobAidCallDetails;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AllCallDurationMeasuresIT extends SpringIntegrationTest {
    @Autowired
    private AllCallDurationMeasures allCallDurationMeasures;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllLocationDimensions allLocationDimensions;

    private UUID flwId = UUID.randomUUID();
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private LocationDimension locationDimension;
    private TimeDimension timeDimension;
    private Long callerId;
    private String operator;
    private String locationId;

    @Before
    public void setUp() {
        tearDown();
        operator = "operator";
        callerId = 1234L;
        locationId = "locationId";
        frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, operator, "circle", "name", "ASHA", "REGISTERED", flwId, null);
        locationDimension = new LocationDimension("locationId", "", "", "", "", "VALID");
        timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
    }

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(AllCallDurationMeasures.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldFindCallDurationMeasuresByCallerId() {
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", callerId, 20, DateTime.now().minusSeconds(10), DateTime.now(), "some", 123));

        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByCallerId(callerId);

        assertEquals(1, callDurationMeasureList.size());
        assertEquals(callerId, callDurationMeasureList.get(0).getCalledNumber());
        assertEquals(operator, callDurationMeasureList.get(0).getOperator());
    }

    @Test
    public void shouldFindCallDurationMeasuresByLocationId() {
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", callerId, 20, DateTime.now().minusSeconds(10), DateTime.now(), "some", 123));

        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByLocationId(locationId);

        assertEquals(1, callDurationMeasureList.size());
        assertEquals(callerId, callDurationMeasureList.get(0).getCalledNumber());
    }

    @Test
    public void shouldGetFlwUsageDetailsByYearAndMonth() {
        Long callerId = 919988776655L;
        Long calledNumber = 5771181L;
        DateTime now = new DateTime(2012, 10, 10, 0, 0);

        jobAidSetup(callerId, calledNumber, now);
        timeDimensionSetup(now, now.plusDays(1));
        certificateCourseSetup(callerId, 5771102L, now);
        certificateCourseSetup(callerId, 5771102L, now.plusDays(1));

        List<CallUsageDetails> callUsageDetailsByYearAndMonth = allCallDurationMeasures.getCallUsageDetailsByMonthAndYear(callerId);

        assertEquals(1, callUsageDetailsByYearAndMonth.size());
        assertEquals(now.getYear(), (int) callUsageDetailsByYearAndMonth.get(0).getYear());
        assertEquals(now.getMonthOfYear(), (int) callUsageDetailsByYearAndMonth.get(0).getMonth());
        assertEquals(10, (long) callUsageDetailsByYearAndMonth.get(0).getJobAidDurationInPulse());
        assertEquals(20, (long) callUsageDetailsByYearAndMonth.get(0).getCertificateCourseDurationInPulse());
    }

    @Test
    public void shouldGetLast10JobAidCallDetails() {
        Long callerId = 911234567890L;
        Long calledNumber = 5771181L;
        DateTime now = new DateTime(2012, 10, 10, 0, 0);
        jobAidSetup(callerId, calledNumber, now);

        List<CallDurationMeasure> jobAidCallDetails = allCallDurationMeasures.getRecentJobAidCallDetails(callerId);

        assertEquals(1, jobAidCallDetails.size());
        assertEquals(callerId, jobAidCallDetails.get(0).getFrontLineWorkerDimension().getMsisdn());
        assertEquals(600, jobAidCallDetails.get(0).getDuration());
    }

    @Test
    public void shouldGetLast10CertificateCourseCallDetails() {
        Long callerId = 911234567890L;
        DateTime now = DateTime.now();
        timeDimensionSetup(now, now);
        certificateCourseSetup(callerId, 33578057L, now);
        certificateCourseSetup(callerId, 33578057L, now.plusHours(1));

        List<CallDurationMeasure> certificateCourse = allCallDurationMeasures.getRecentCertificateCourseCallDetails(callerId);

        assertEquals(2, certificateCourse.size());
        assertEquals(callerId, certificateCourse.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldFetchCallRecordsThatFallBetweenTheTimeRangeGiven() {
        Long callerId = 911234567890L;
        Long calledNumber = 5771181L;

        timeDimensionSetup(new DateTime(2009, 11, 12, 19, 20, 21), new DateTime(2009, 11, 15, 01, 20, 21));

        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 18, 20, 21), new DateTime(2009, 11, 12, 18, 30, 21)); //same day; exclude

        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 18, 20, 21), new DateTime(2009, 11, 13, 07, 20, 21)); //Starts before and ends after; exclude
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 18, 20, 21), new DateTime(2009, 11, 12, 19, 00, 21)); //Starts before but ends in between; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 19, 20, 21), new DateTime(2009, 11, 12, 20, 20, 21)); //Falls between; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 20, 00, 21), new DateTime(2009, 11, 13, 8, 00, 21)); //Starts in between but ends after; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 14, 01, 20, 21), new DateTime(2009, 11, 14, 02, 20, 21)); //next day; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 14, 19, 20, 21), new DateTime(2009, 11, 15, 01, 20, 21)); //next day; exclude

        List<JobAidCallDetails> jobAidCallDetails = allCallDurationMeasures.getJobAidNighttimeCallDetails(callerId, new LocalDate(2009, 11, 12), new LocalDate(2009, 11, 14));

        assertEquals(4, jobAidCallDetails.size());

        assertJobAidCallDetails(jobAidCallDetails.get(0), new DateTime(2009, 11, 12, 18, 20, 21), new DateTime(2009, 11, 12, 19, 00, 21));
        assertJobAidCallDetails(jobAidCallDetails.get(1), new DateTime(2009, 11, 12, 19, 20, 21), new DateTime(2009, 11, 12, 20, 20, 21));
        assertJobAidCallDetails(jobAidCallDetails.get(2), new DateTime(2009, 11, 12, 20, 00, 21), new DateTime(2009, 11, 13, 8, 00, 21));
        assertJobAidCallDetails(jobAidCallDetails.get(3), new DateTime(2009, 11, 14, 01, 20, 21), new DateTime(2009, 11, 14, 02, 20, 21));
    }

    @Test
    public void shouldTransferRecords() {
        long msisdnOfFlw1 = 123L;
        long msisdnOfFLw2 = 1234L;
        FrontLineWorkerDimension flw1 = jobAidSetup(msisdnOfFlw1, 456L, DateTime.now().minusYears(2));
        FrontLineWorkerDimension flw2 = jobAidSetup(msisdnOfFLw2, 4567L, DateTime.now().minusYears(1));

        allCallDurationMeasures.transfer(CallDurationMeasure.class, flw1.getId(), flw2.getId());

        List<CallDurationMeasure> callDurationMeasureByCallerId2 = allCallDurationMeasures.findByCallerId(msisdnOfFLw2);
        List<CallDurationMeasure> callDurationMeasureByCallerId1 = allCallDurationMeasures.findByCallerId(msisdnOfFlw1);

        assertEquals(4, callDurationMeasureByCallerId2.size());
        assertEquals(0, callDurationMeasureByCallerId1.size());
    }

    @Test
    public void shouldGetJobAidCallDetailsForAGivenTimeRange() {
        Long callerId = 911234567890L;
        Long calledNumber = 5771181L;

        timeDimensionSetup(new DateTime(2009, 11, 12, 19, 20, 21), new DateTime(2009, 11, 15, 01, 20, 21));

        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 18, 59, 59), new DateTime(2009, 11, 13, 07, 00, 00)); //Edge case; exclude
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 18, 59, 59), new DateTime(2009, 11, 13, 06, 59, 59)); //Edge case; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 12, 19, 00, 00), new DateTime(2009, 11, 13, 07, 00, 00)); //Edge case; include

        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 13, 07, 00, 00), new DateTime(2009, 11, 13, 18, 59, 59)); //Edge case; exclude
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 13, 06, 59, 59), new DateTime(2009, 11, 13, 18, 59, 59)); //Edge case; include
        jobAidSetup(callerId, calledNumber, new DateTime(2009, 11, 13, 07, 00, 00), new DateTime(2009, 11, 13, 19, 00, 00)); //Edge case; include

        List<JobAidCallDetails> jobAidCallDetails = allCallDurationMeasures.getJobAidNighttimeCallDetails(callerId, new LocalDate(2009, 11, 12), new LocalDate(2009, 11, 14));

        assertEquals(4, jobAidCallDetails.size());
        assertJobAidCallDetails(jobAidCallDetails.get(0), new DateTime(2009, 11, 12, 18, 59, 59), new DateTime(2009, 11, 13, 06, 59, 59));
        assertJobAidCallDetails(jobAidCallDetails.get(1), new DateTime(2009, 11, 12, 19, 00, 00), new DateTime(2009, 11, 13, 07, 00, 00));
        assertJobAidCallDetails(jobAidCallDetails.get(2), new DateTime(2009, 11, 13, 06, 59, 59), new DateTime(2009, 11, 13, 18, 59, 59));
        assertJobAidCallDetails(jobAidCallDetails.get(3), new DateTime(2009, 11, 13, 07, 00, 00), new DateTime(2009, 11, 13, 19, 00, 00));
    }

    private void assertJobAidCallDetails(JobAidCallDetails jobAidCallDetails, DateTime startTime, DateTime endTime) {
        assertEquals(startTime, jobAidCallDetails.getStartTime());
        assertEquals(endTime, jobAidCallDetails.getEndTime());
        assertEquals(Minutes.minutesBetween(startTime, endTime).getMinutes(), (int) jobAidCallDetails.getDurationInPulse());
    }

    private void timeDimensionSetup(DateTime startDate, DateTime endDate) {
        DateTime next = startDate;
        while (!next.isAfter(endDate)) {
            allTimeDimensions.makeFor(next);
            next = next.plusDays(1);
        }
    }

    private void certificateCourseSetup(Long callerId, long calledNumber, DateTime now) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.getFor(now);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 600, now, now.plusMinutes(10), "CALL", 10));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 540, now.plusSeconds(60), now.plusMinutes(10), "CERTIFICATECOURSE", 9));
    }

    private FrontLineWorkerDimension jobAidSetup(Long callerId, long calledNumber, DateTime now) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(now.minusDays(1));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 600, now, now.plusMinutes(10), "CALL", 10));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 540, now.plusSeconds(60), now.plusMinutes(10), "JOBAID", 9));
        return frontLineWorkerDimension;
    }

    private void jobAidSetup(Long callerId, long calledNumber, DateTime startTime, DateTime endTime) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.getFor(startTime);

        int callseconds = Seconds.secondsBetween(startTime, endTime).getSeconds();
        DateTime jobAidStartTime = startTime.plusSeconds(60);
        int jobAidSeconds = Seconds.secondsBetween(jobAidStartTime, endTime).getSeconds();

        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, callseconds, startTime, endTime, "CALL", (int) Math.floor(callseconds / 60)));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, jobAidSeconds, jobAidStartTime, endTime, "JOBAID", (int) Math.floor(jobAidSeconds / 60)));
    }
}
