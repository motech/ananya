package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", "flwGuid");
        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.add(locationDimension);
        allCallDurationMeasures.add(new CallDurationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "callId", callerId, 20, DateTime.now().minusSeconds(10), DateTime.now(), "some"));

        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByCallerId(callerId);

        assertEquals(1, callDurationMeasureList.size());
        assertEquals(callerId, callDurationMeasureList.get(0).getCalledNumber());
    }
}
