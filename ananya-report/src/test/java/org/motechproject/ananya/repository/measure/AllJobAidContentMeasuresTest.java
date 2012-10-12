package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllJobAidContentMeasuresTest {
    @Mock
    private DataAccessTemplate template;

    @Test
    public void shouldGetFilteredMsisdns() {
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        TimeDimension timeDimension1 = new TimeDimension(DateTime.now().minusDays(1));
        TimeDimension timeDimension2 = new TimeDimension(DateTime.now().minusDays(2));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(911234567890L, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name(), "flwGuid1");
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(911234567891L, "airtel", "bihar", "name", "ANM", RegistrationStatus.REGISTERED.name(), "flwGuid2");
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
    }

    public void shouldUpdateAllJobAidContentMeasures() {
        ArrayList<JobAidContentMeasure> expectedJobAidContentMeasureList = new ArrayList<JobAidContentMeasure>();
        AllJobAidContentMeasures allJobAidContentMeasures = new AllJobAidContentMeasures(template);

        allJobAidContentMeasures.updateAll(expectedJobAidContentMeasureList);

        verify(template).saveOrUpdateAll(expectedJobAidContentMeasureList);
    }
}
