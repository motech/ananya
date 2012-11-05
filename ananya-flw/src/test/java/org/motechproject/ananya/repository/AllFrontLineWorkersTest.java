package org.motechproject.ananya.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllFrontLineWorkersTest extends SpringBaseIT {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    private UUID flwId = UUID.randomUUID();

    @Before
    public void setUp() throws IOException {
        allFrontLineWorkers.removeAll();
    }

    @Test
    public void shouldAddAndRetrieveRecord() {
        String msisdn = "919988776655";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "village", 2, 3, 4, null, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, location, null, flwId);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        FrontLineWorker frontLineWorkerFromDb = frontLineWorkers.get(0);
        assertEquals(msisdn, frontLineWorkerFromDb.getMsisdn());
        assertEquals("S01D002B003V004", frontLineWorkerFromDb.getLocationId());
        assertEquals(flwId, frontLineWorkerFromDb.getFlwId());
        assertTrue(frontLineWorkerFromDb.isAnganwadi());
    }

    @Test
    public void shouldFetchFLWsByLocationId() {
        String msisdn = "919988776655";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "village", 2, 3, 4, null, null);
        Location location1 = new Location("distr1ict", "bloc1k", "vi1llage", 2, 3, 5, null, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, location, null, UUID.randomUUID());
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("911234567890", "name", designation, location1, null, UUID.randomUUID());
        allFrontLineWorkers.add(frontLineWorker);
        allFrontLineWorkers.add(frontLineWorker1);
        markForDeletion(frontLineWorker);

        List<FrontLineWorker> flwFromDb = allFrontLineWorkers.findByLocationId(location.getExternalId());

        assertEquals(1, flwFromDb.size());
        assertEquals(frontLineWorker.getMsisdn(), flwFromDb.get(0).getMsisdn());
    }

    @Test
    public void shouldRetrieveFrontLineWorkerByMSISDN() {
        String msisdn = "919988776655";
        Designation designation = Designation.AWW;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, new Location(), null, flwId);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        FrontLineWorker dbFrontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        assertEquals(msisdn, dbFrontLineWorker.getMsisdn());
    }

    @Test
    public void shouldRetrieveAllFrontLineWorkersForMsisdn() {
        String msisdn = "654321";
        String anotherMsisdn = "4321";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "airtel", "circle");
        FrontLineWorker duplicateFrontLineWorker = new FrontLineWorker(msisdn, "airtel", "circle");
        FrontLineWorker notTheSameFLW = new FrontLineWorker(anotherMsisdn, "airtel", "circle");
        allFrontLineWorkers.add(frontLineWorker);
        allFrontLineWorkers.add(duplicateFrontLineWorker);
        allFrontLineWorkers.add(notTheSameFLW);
        markForDeletion(frontLineWorker);
        markForDeletion(duplicateFrontLineWorker);
        markForDeletion(notTheSameFLW);

        List<FrontLineWorker> allForMsisdn = allFrontLineWorkers.getAllForMsisdn(msisdn);

        assertEquals(2, allForMsisdn.size());
    }

    @Test
    public void shouldRetrieveOnlyRequestedCountOfMsisdns() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9999991", "airtel", "circle");
        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9999992", "airtel", "circle");
        FrontLineWorker frontLineWorker3 = new FrontLineWorker("9999993", "airtel", "circle");
        FrontLineWorker frontLineWorker4 = new FrontLineWorker("9999994", "airtel", "circle");
        FrontLineWorker frontLineWorker5 = new FrontLineWorker("9999995", "airtel", "circle");
        FrontLineWorker frontLineWorker6 = new FrontLineWorker("9999996", "airtel", "circle");
        allFrontLineWorkers.add(frontLineWorker1);
        allFrontLineWorkers.add(frontLineWorker2);
        allFrontLineWorkers.add(frontLineWorker3);
        allFrontLineWorkers.add(frontLineWorker4);
        allFrontLineWorkers.add(frontLineWorker5);
        allFrontLineWorkers.add(frontLineWorker6);

        List<FrontLineWorker> fetchedFrontLineWorkers;
        fetchedFrontLineWorkers = allFrontLineWorkers.getMsisdnsFrom(frontLineWorker3.getMsisdn(), 2);

        assertEquals(2, fetchedFrontLineWorkers.size());
        assertEquals(frontLineWorker3.getMsisdn(), fetchedFrontLineWorkers.get(0).getMsisdn());
        assertEquals(frontLineWorker4.getMsisdn(), fetchedFrontLineWorkers.get(1).getMsisdn());

        fetchedFrontLineWorkers = allFrontLineWorkers.getMsisdnsFrom(frontLineWorker6.getMsisdn(), 3);
        assertEquals(1, fetchedFrontLineWorkers.size());
        assertEquals(frontLineWorker6.getMsisdn(), fetchedFrontLineWorkers.get(0).getMsisdn());
    }

    @Test
    public void shouldRetrieveFrontLineWorkerByFlwId() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("919988776655", "name", Designation.ANM, new Location(), null, flwId);
        allFrontLineWorkers.add(frontLineWorker);
        markForDeletion(frontLineWorker);

        FrontLineWorker dbFrontLineWorker = allFrontLineWorkers.findByFlwId(flwId);

        assertEquals(flwId, dbFrontLineWorker.getFlwId());
    }
}
