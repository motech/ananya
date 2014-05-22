package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.*;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.dimension.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AllJobAidContentMeasuresIT extends SpringIntegrationTest {

    @Autowired
    AllCourseItemMeasures allCourseItemMeasures;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllJobAidContentDimensions allJobAidContentDimensions;

    @Autowired
    AllJobAidContentMeasures allJobAidContentMeasures;

    @Autowired
    AllLocationDimensions allLocationDimensions;

    @Autowired
    AllLanguageDimension allLanguageDimension;
    private LocationDimension locationDimension;
    private LanguageDimension languageDimension;
    private TimeDimension timeDimension;
    private JobAidContentDimension jobAidContentDimension;
    private String locationId = "locationId";

    @Before
    public void setUp() {
        tearDown();
        locationDimension = new LocationDimension(locationId, "", "", "", "", "VALID");
        languageDimension = new LanguageDimension("bhojpuri", "bho", "badhai ho..");
        timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        jobAidContentDimension = new JobAidContentDimension("1234567", null, "name", "type");
        allJobAidContentDimensions.add(jobAidContentDimension);
        allLanguageDimension.add(languageDimension);
    }

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
        template.deleteAll(template.loadAll(JobAidContentMeasure.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(LanguageDimension.class));
    }

    @Test
    public void shouldGetFilteredMsisdns() {
        TimeDimension now = new TimeDimension(DateTime.now());
        TimeDimension timeDimension1 = timeDimension;
        TimeDimension timeDimension2 = new TimeDimension(DateTime.now().minusDays(2));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(911234567890L, null, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(911234567891L, null, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);

        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension("1234567", null, "name", "type");
        JobAidContentMeasure jobAidContentMeasure = new JobAidContentMeasure("callId", frontLineWorkerDimension, locationDimension, jobAidContentDimension, now, languageDimension, DateTime.now(), 123, 12);
        JobAidContentMeasure jobAidContentMeasure1 = new JobAidContentMeasure("callId", frontLineWorkerDimension1, locationDimension, jobAidContentDimension, timeDimension1, languageDimension, DateTime.now(), 123, 12);
        JobAidContentMeasure jobAidContentMeasure2 = new JobAidContentMeasure("callId", frontLineWorkerDimension1, locationDimension, jobAidContentDimension, timeDimension2, languageDimension, DateTime.now(), 123, 12);
        template.save(now);
        template.save(timeDimension2);
        template.save(frontLineWorkerDimension);
        template.save(frontLineWorkerDimension1);
        template.save(jobAidContentDimension);
        template.save(jobAidContentMeasure);
        template.save(jobAidContentMeasure1);
        template.save(jobAidContentMeasure2);

        List<Long> filteredFrontLineWorkerMsisdns = allJobAidContentMeasures.getFilteredFrontLineWorkerMsisdns(DateTime.now().toDate(), DateTime.now().toDate());

        assertEquals(1, filteredFrontLineWorkerMsisdns.size());
        assertEquals((Long) 911234567890L, filteredFrontLineWorkerMsisdns.get(0));
    }

    @Test
    public void shouldFetchAllJobAidContentMeasuresForACallerId() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension("1234567", null, "name", "type");
        allJobAidContentDimensions.add(jobAidContentDimension);
        allJobAidContentMeasures.add(new JobAidContentMeasure("callId", frontLineWorkerDimension, locationDimension, jobAidContentDimension, timeDimension, languageDimension, DateTime.now(), 23, 23));

        List<JobAidContentMeasure> jobAidContentMeasureList = allJobAidContentMeasures.findByCallerId(callerId);

        assertEquals(1, jobAidContentMeasureList.size());
        assertEquals(callerId, jobAidContentMeasureList.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldFetchAllJobAidContentMeasuresForALocationId() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);

        allJobAidContentMeasures.add(new JobAidContentMeasure("callId", frontLineWorkerDimension, locationDimension, jobAidContentDimension, timeDimension, languageDimension, DateTime.now(), 23, 23));

        List<JobAidContentMeasure> jobAidContentMeasureList = allJobAidContentMeasures.findByLocationId(locationId);

        assertEquals(1, jobAidContentMeasureList.size());
        assertEquals(callerId, jobAidContentMeasureList.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldTransferRecords() {
        Long msisdnOfFlw1 = 1234L;
        FrontLineWorkerDimension flw1 = allFrontLineWorkerDimensions.createOrUpdate(msisdnOfFlw1, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        Long msisdnOfFlw2 = 12345L;
        FrontLineWorkerDimension flw2 = allFrontLineWorkerDimensions.createOrUpdate(msisdnOfFlw2, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        allJobAidContentMeasures.add(new JobAidContentMeasure("callId", flw1, locationDimension, jobAidContentDimension, timeDimension, languageDimension, DateTime.now(), 23, 23));
        allJobAidContentMeasures.add(new JobAidContentMeasure("callId", flw2, locationDimension, jobAidContentDimension, timeDimension, languageDimension, DateTime.now(), 23, 23));

        allJobAidContentMeasures.transfer(JobAidContentMeasure.class, flw1.getId(), flw2.getId());
        assertEquals(0, allJobAidContentMeasures.findByCallerId(msisdnOfFlw1).size());
        assertEquals(2, allJobAidContentMeasures.findByCallerId(msisdnOfFlw2).size());
    }
}
