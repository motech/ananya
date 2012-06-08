package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class AllFrontLineWorkersTest extends FrontLineWorkerBaseIT {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldAddAndRetrieveRecord() {
        String msisdn = "919988776655";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "village", 2, 3, 4);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, location, RegistrationStatus.REGISTERED);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        FrontLineWorker frontLineWorkerFromDb = frontLineWorkers.get(0);
        assertEquals(msisdn, frontLineWorkerFromDb.getMsisdn());
        assertEquals("S01D002B003V004", frontLineWorkerFromDb.getLocationId());
        assertTrue(frontLineWorkerFromDb.isAnganwadi());
    }

    @Test
    public void shouldRetrieveFrontLineWorkerByMSISDN() {
        String msisdn = "919988776655";
        Designation designation = Designation.AWW;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, new Location(), RegistrationStatus.REGISTERED);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        FrontLineWorker dbFrontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        assertEquals(msisdn, dbFrontLineWorker.getMsisdn());
    }

    @Test
    public void shouldRetrieveAllFrontLineWorkersForMsisdn() {
        String msisdn = "654321";
        String anotherMsisdn = "4321";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "airtel");
        FrontLineWorker duplicateFrontLineWorker = new FrontLineWorker(msisdn, "airtel");
        FrontLineWorker notTheSameFLW = new FrontLineWorker(anotherMsisdn, "airtel");
        allFrontLineWorkers.add(frontLineWorker);
        allFrontLineWorkers.add(duplicateFrontLineWorker);
        allFrontLineWorkers.add(notTheSameFLW);
        markForDeletion(frontLineWorker);
        markForDeletion(duplicateFrontLineWorker);
        markForDeletion(notTheSameFLW);

        List<FrontLineWorker> allForMsisdn = allFrontLineWorkers.getAllForMsisdn(msisdn);

        assertEquals(2, allForMsisdn.size());
    }
}
