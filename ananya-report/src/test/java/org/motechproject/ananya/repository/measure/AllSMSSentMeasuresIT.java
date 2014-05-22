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
    private TimeDimension timeDimension;
    private LocationDimension locationDimension;
    private FrontLineWorkerDimension frontLineWorker;
    private String smsReferenceNumber;

    @Before
    public void setUp() {
        tearDown();
        smsReferenceNumber = "refNo";
        frontLineWorker = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf("9876"), null, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        timeDimension = allTimeDimensions.makeFor(DateTime.now());
        locationDimension = new LocationDimension("locationId", "state", "district", "block", "panchayat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
    }

    @After
    public void tearDown() {
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
        template.save(new SMSSentMeasure(2, "23123123", false, new FrontLineWorkerDimension(9876543210L, null, "", "", "", "", "", flwId, null), null, null));
    }

    @Test
    public void shouldFetchBasedOnFLW() {
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorker.getId());

        assertEquals(smsReferenceNumber, smsSentMeasure.getSmsReferenceNumber());
    }

    @Test
    public void shouldFetchBasedOnFLWCallerId() {
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByCallerId(frontLineWorker.getMsisdn());

        assertEquals(smsReferenceNumber, smsSentMeasureList.get(0).getSmsReferenceNumber());
    }

    @Test
    public void shouldFetchBasedOnLocationId() {
        String locationId = "locationId";
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, frontLineWorker, timeDimension, locationDimension));

        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByLocationId(locationId);

        assertEquals(smsReferenceNumber, smsSentMeasureList.get(0).getSmsReferenceNumber());
    }

    @Test
    public void shouldTransferRecords() {
        Long msisdnOfFlw1 = 1234L;
        FrontLineWorkerDimension flw1 = allFrontLineWorkerDimensions.createOrUpdate(msisdnOfFlw1, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        Long msisdnOfFlw2 = 12345L;
        FrontLineWorkerDimension flw2 = allFrontLineWorkerDimensions.createOrUpdate(msisdnOfFlw2, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, flw1, timeDimension, locationDimension));
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber, true, flw2, timeDimension, locationDimension));

        allSMSSentMeasures.transfer(SMSSentMeasure.class, flw1.getId(), flw2.getId());

        List<SMSSentMeasure> smsSentMeasureOfFlw1 = allSMSSentMeasures.findByCallerId(msisdnOfFlw1);
        List<SMSSentMeasure> smsSentMeasureOfFlw2 = allSMSSentMeasures.findByCallerId(msisdnOfFlw2);
        assertEquals(0, smsSentMeasureOfFlw1.size());
        assertEquals(2, smsSentMeasureOfFlw2.size());
    }
}
