package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallUsageDetails;
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

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(AllCallDurationMeasures.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldFindCallDurationMeasuresByCallerId() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId);
        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "", "VALID");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", callerId, 20, DateTime.now().minusSeconds(10), DateTime.now(), "some", 123));

        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByCallerId(callerId);

        assertEquals(1, callDurationMeasureList.size());
        assertEquals(callerId, callDurationMeasureList.get(0).getCalledNumber());
    }

    @Test
    public void shouldFindCallDurationMeasuresByLocationId() {
        Long callerId = 1234L;
        String locationId = "locationId";
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId);
        LocationDimension locationDimension = new LocationDimension(locationId, "", "", "", "VALID");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", callerId, 20, DateTime.now().minusSeconds(10), DateTime.now(), "some", 123));

        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByLocationId(locationId);

        assertEquals(1, callDurationMeasureList.size());
        assertEquals(callerId, callDurationMeasureList.get(0).getCalledNumber());
    }

    @Test
    public void shouldGetFlwUsageDetailsByYearAndMonth() {
        Long callerId = 919988776655L;
        Long calledNumber = 5771181L;
        DateTime now = DateTime.now();
        jobAidSetup(callerId, calledNumber, now);
        certificateCourseSetup(callerId, 5771102L, now);

        List<CallUsageDetails> callUsageDetailsByYearAndMonth = allCallDurationMeasures.getCallUsageDetailsByMonthAndYear(callerId);

        assertEquals(1, callUsageDetailsByYearAndMonth.size());
        assertEquals(now.getYear(), (int) callUsageDetailsByYearAndMonth.get(0).getYear());
        assertEquals(now.getMonthOfYear(), (int) callUsageDetailsByYearAndMonth.get(0).getMonth());
        assertEquals(600L, (long) callUsageDetailsByYearAndMonth.get(0).getJobAidDurationInSec());
        assertEquals(610L, (long) callUsageDetailsByYearAndMonth.get(0).getCertificateCourseDurationInSec());
    }

    @Test
    public void shouldGetLast10JobAidCallDetails() {
        Long callerId = 911234567890L;
        Long calledNumber = 5771181L;
        DateTime now = DateTime.now();
        jobAidSetup(callerId, calledNumber, now);
        certificateCourseSetup(callerId, 5771102L, now);

        List<CallDurationMeasure> jobAidCallDetails = allCallDurationMeasures.getRecentJobAidCallDetails(callerId);

        assertEquals(1, jobAidCallDetails.size());
        assertEquals(callerId, jobAidCallDetails.get(0).getFrontLineWorkerDimension().getMsisdn());
        assertEquals(600, jobAidCallDetails.get(0).getDuration());
    }

    @Test
    public void shouldGetLast10CertificateCourseCallDetails() {
        Long callerId = 911234567890L;
        Long calledNumber = 5771181L;
        DateTime now = DateTime.now();
        jobAidSetup(callerId, calledNumber, now);
        certificateCourseSetup(callerId, 33578057L, now);

        List<CallDurationMeasure> certificateCourse = allCallDurationMeasures.getRecentCertificateCourseCallDetails(callerId);

        assertEquals(2, certificateCourse.size());
        assertEquals(callerId, certificateCourse.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    private void certificateCourseSetup(Long callerId, long calledNumber, DateTime now) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId);
        LocationDimension locationDimension = new LocationDimension("locationId", "D1", "", "", null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(now.minusDays(2));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 10, now.minusDays(1), now.minusDays(1).plusSeconds(10), "CALL", 1));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 600, now.minusSeconds(60), now, "CALL", 1));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 590, now, now.plusMinutes(10), "CERTIFICATECOURSE", 1));
    }

    private void jobAidSetup(Long callerId, long calledNumber, DateTime now) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId);
        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "", null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(now.minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 600, now.minusSeconds(60), now, "CALL", 1));
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", calledNumber, 590, now, now.plusMinutes(10), "JOBAID", 1));
    }
}
