package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllFrontLineWorkersTest extends FrontLineWorkerBaseIT {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldAddAndRetrieveRecord() {
        String msisdn = "9901";
        Designation designation = Designation.ANGANWADI;
        Location location = new Location("district", "block", "village", 2, 3, 4);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, location, RegistrationStatus.REGISTERED);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        assertEquals(msisdn, frontLineWorkers.get(0).getMsisdn());
        assertEquals("S01D002B003V004", frontLineWorkers.get(0).getLocationId());
        assertTrue(frontLineWorkers.get(0).isAnganwadi());
    }

    @Test
    public void shouldRetrieveFrontLineWorkerByMSISDN() {
        String msisdn = "9901";
        Designation designation = Designation.ANGANWADI;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, new Location(), RegistrationStatus.REGISTERED);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        FrontLineWorker dbFrontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        assertEquals(msisdn, dbFrontLineWorker.getMsisdn());
    }


    @Test
    public void shouldRetrieveFrontLineWorkerByRegisteredDate() {
        String msisdn = "9901";
        Designation designation = Designation.ANGANWADI;
        DateTime registeredDate = DateUtil.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", designation, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.setRegisteredDate(registeredDate);

        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> dbFrontLineWorkers = allFrontLineWorkers.findByRegisteredDate(registeredDate);

        assertEquals(dbFrontLineWorkers.size(), 1);
        assertEquals(msisdn, dbFrontLineWorkers.get(0).getMsisdn());
    }


}
