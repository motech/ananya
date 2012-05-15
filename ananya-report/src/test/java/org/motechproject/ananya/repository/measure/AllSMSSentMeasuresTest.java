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

import static junit.framework.Assert.assertEquals;

public class AllSMSSentMeasuresTest extends SpringIntegrationTest{

    @Autowired 
    AllSMSSentMeasures allSMSSentMeasures;
    
    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    
    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllLocationDimensions allLocationDimensions;
    
    @Before
    public void setUp(){
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @After
    public void TearDown(){
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }
    
    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenFLWDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, null, new TimeDimension(DateTime.now()),null));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenTimeDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, new FrontLineWorkerDimension(9876543210L, "", "", "", "", ""), null,null));
    }
    
    @Test
    public void shouldFetchBasedOnFLW(){
        String smsReferenceNumber = "refNo";
        FrontLineWorkerDimension frontLineWorker = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf("9876"), "operator", "circle", "name", "ASHA", "REGISTERED");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        LocationDimension locationDimension = new LocationDimension("locationId", "district", "block", "panchayat");
        allLocationDimensions.add(locationDimension);
        allSMSSentMeasures.save(new SMSSentMeasure(1, smsReferenceNumber,true,frontLineWorker, timeDimension, locationDimension));

        SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorker.getId());

        assertEquals(smsReferenceNumber,smsSentMeasure.getSmsReferenceNumber());
    }
}
