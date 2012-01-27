package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Designation;
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
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, designation);
        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        assertEquals(msisdn, frontLineWorkers.get(0).getMsisdn());
        assertTrue(frontLineWorkers.get(0).isAnganwadi());
    }

    @Test
    public void shouldRetrieveFrontLineWorkerByMSISDN() {
        String msisdn = "9901";
        Designation designation = Designation.ANGANWADI;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, designation);
        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);

        FrontLineWorker dbFrontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        assertEquals(msisdn, dbFrontLineWorker.getMsisdn());
    }
}
