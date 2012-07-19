package org.motechproject.ananya.seed.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerSeedServiceTest {

    private FrontLineWorkerSeedService frontLineWorkerSeedService;

    @Before
    public void setUp(){
     frontLineWorkerSeedService = new FrontLineWorkerSeedService();
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformation() {
        Location completeLocation = new Location("district", "block", "panchayat", 1, 1, 1);
        Location incompleteLocation = new Location("district", "block", "", 1, 1, 0);
        Location defaultLocation = Location.getDefaultLocation();

        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", "name", Designation.ANM, completeLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", "", Designation.ANM, completeLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", "name", Designation.INVALID, completeLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, defaultLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, incompleteLocation, RegistrationStatus.REGISTERED);
        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", "", null, defaultLocation, RegistrationStatus.REGISTERED);

        assertEquals(RegistrationStatus.REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithCompleteDetails, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithoutName, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithoutDesignation, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithInvalidDesignation, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithDefaultLocation, defaultLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithIncompleteLocation, incompleteLocation));
        assertEquals(RegistrationStatus.UNREGISTERED,
                frontLineWorkerSeedService.deduceRegistrationStatus(flwWithNoDetails, defaultLocation));
    }
}
