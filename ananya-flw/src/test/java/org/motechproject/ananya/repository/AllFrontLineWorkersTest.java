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
    public void shouldRetrieveFrontLineWorkerByRegistrationWindow() {

        Designation designation = Designation.ANGANWADI;
        DateTime date1 = DateUtil.now();
        DateTime date2 = date1.plusDays(5);
        DateTime date3 = date1.plusDays(7);

        FrontLineWorker frontLineWorker1 = new FrontLineWorker("111", "name", designation, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker1.setRegisteredDate(date1);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker("222", "name", designation, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker2.setRegisteredDate(date2);
        FrontLineWorker frontLineWorker3 = new FrontLineWorker("333", "name", designation, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker3.setRegisteredDate(date3);

        allFrontLineWorkers.add(frontLineWorker1);
        allFrontLineWorkers.add(frontLineWorker2);
        allFrontLineWorkers.add(frontLineWorker3);

        markForDeletion(frontLineWorker1);
        markForDeletion(frontLineWorker2);
        markForDeletion(frontLineWorker3);

        assertEquals(1, allFrontLineWorkers.findByRegisteredDate(date1, date1).size());
        assertEquals(2, allFrontLineWorkers.findByRegisteredDate(date1, date2).size());
        assertEquals(3, allFrontLineWorkers.findByRegisteredDate(date1, date3).size());
        assertEquals(2, allFrontLineWorkers.findByRegisteredDate(date2, date3).size());

    }


}
