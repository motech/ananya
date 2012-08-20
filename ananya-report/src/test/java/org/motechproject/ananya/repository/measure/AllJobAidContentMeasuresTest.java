package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllJobAidContentMeasuresTest extends SpringIntegrationTest {

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

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
        template.deleteAll(template.loadAll(JobAidContentMeasure.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldGetFilteredMsisdns() {
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        TimeDimension timeDimension1 = new TimeDimension(DateTime.now().minusDays(1));
        TimeDimension timeDimension2 = new TimeDimension(DateTime.now().minusDays(2));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(911234567890L, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name());
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(911234567891L, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name());
        LocationDimension locationDimension = new LocationDimension("S02123431243", "D1", "B1", "P1");
        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension("1234567", null, "name", "fileName", "type", 123);
        JobAidContentMeasure jobAidContentMeasure = new JobAidContentMeasure("callId", frontLineWorkerDimension, locationDimension, jobAidContentDimension, timeDimension, DateTime.now(), 123, 12);
        JobAidContentMeasure jobAidContentMeasure1 = new JobAidContentMeasure("callId", frontLineWorkerDimension1, locationDimension, jobAidContentDimension, timeDimension1, DateTime.now(), 123, 12);
        JobAidContentMeasure jobAidContentMeasure2 = new JobAidContentMeasure("callId", frontLineWorkerDimension1, locationDimension, jobAidContentDimension, timeDimension2, DateTime.now(), 123, 12);
        template.save(timeDimension);
        template.save(timeDimension1);
        template.save(timeDimension2);
        template.save(frontLineWorkerDimension);
        template.save(frontLineWorkerDimension1);
        template.save(locationDimension);
        template.save(jobAidContentDimension);
        template.save(jobAidContentMeasure);
        template.save(jobAidContentMeasure1);
        template.save(jobAidContentMeasure2);

        List<Long> filteredFrontLineWorkerMsisdns = allJobAidContentMeasures.getFilteredFrontLineWorkerMsisdns(DateTime.now().toDate(), DateTime.now().toDate());

        assertEquals(1, filteredFrontLineWorkerMsisdns.size());
        assertEquals((Long)911234567890L, filteredFrontLineWorkerMsisdns.get(0));
    }
}
