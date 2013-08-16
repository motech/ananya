package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AllSMSSentMeasuresIT extends SpringIntegrationTest {

    @Autowired
    AllSMSSentMeasures allSMSSentMeasures;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllLocationDimensions allLocationDimensions;

    private UUID flwId = UUID.randomUUID();

    @Before
    public void setUp() {
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @After
    public void TearDown() {
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenFLWDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, null, new TimeDimension(DateTime.now()), null));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenTimeDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, new FrontLineWorkerDimension(9876543210L, "", "", "", "", "", flwId, null), null, null));
    }

    @Test
    public void shouldFetchBasedOnFLW() {
        String smsReferenceNumber = "refNo";
        FrontLineWorkerDimension frontLineWorker = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf("9876"), "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        LocationDimension locationDimension = new LocationDimension("locationId", "state", "district", "block", "panchayat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorker.getId());

        assertEquals(smsReferenceNumber, smsSentMeasure.getSmsReferenceNumber());
    }

    @Test
    public void shouldFetchBasedOnFLWCallerId() {
        String smsReferenceNumber = "refNo";
        FrontLineWorkerDimension frontLineWorker = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf("9876"), "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        LocationDimension locationDimension = new LocationDimension("locationId", "state", "district", "block", "panchayat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByCallerId(frontLineWorker.getMsisdn());

        assertEquals(smsReferenceNumber, smsSentMeasureList.get(0).getSmsReferenceNumber());
    }

    @Test
    public void shouldFetchBasedOnLocationId() {
        String smsReferenceNumber = "refNo";
        String locationId = "locationId";
        FrontLineWorkerDimension frontLineWorker = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf("9876"), "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        LocationDimension locationDimension = new LocationDimension(locationId, "state", "district", "block", "panchayat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByLocationId(locationId);

        assertEquals(smsReferenceNumber, smsSentMeasureList.get(0).getSmsReferenceNumber());
    }
}
