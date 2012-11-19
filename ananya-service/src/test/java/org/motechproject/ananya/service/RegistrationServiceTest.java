package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.requests.FLWStatusChangeRequest;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationServiceTest {
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;
    @Mock
    private LocationService locationService;
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSSentMeasureService smsSentMeasureService;
    @Mock
    private LocationRegistrationService locationRegistrationService;

    @Captor
    private ArgumentCaptor<List<FLWStatusChangeRequest>> flwStatusChangeRequestCaptor;
    private RegistrationService registrationService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(registrationMeasureService, courseItemMeasureService, callDurationMeasureService, jobAidContentMeasureService, smsSentMeasureService, frontLineWorkerService, frontLineWorkerDimensionService);
    }

    @Test
    public void shouldUpdateLocationInAllMeasures() {
        String newLocationId = "newLocationId";
        String oldLocationId = "oldLocationId";

        registrationService.updateAllLocationReferences(oldLocationId, newLocationId);

        verify(callDurationMeasureService).updateLocation(oldLocationId, newLocationId);
        verify(jobAidContentMeasureService).updateLocation(oldLocationId, newLocationId);
        verify(courseItemMeasureService).updateLocation(oldLocationId, newLocationId);
        verify(smsSentMeasureService).updateLocation(oldLocationId, newLocationId);
        verify(registrationMeasureService).updateLocation(oldLocationId, newLocationId);
    }

    @Test
    public void shouldUpdateLocation() {
        Location oldLocation = new Location("d1", "b1", "p1");
        Location newLocation = new Location("d2", "b2", "p2");
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        String msisdn = "911234567890";
        frontLineWorkers.add(new FrontLineWorker(msisdn, "name", Designation.ANM, oldLocation, DateTime.now(), UUID.randomUUID()));
        when(frontLineWorkerService.updateLocation(oldLocation, newLocation)).thenReturn(frontLineWorkers);

        registrationService.updateLocationOnFLW(oldLocation, newLocation);

        verify(frontLineWorkerService).updateLocation(oldLocation, newLocation);
        verify(frontLineWorkerDimensionService).updateStatus(flwStatusChangeRequestCaptor.capture());
        List<FLWStatusChangeRequest> flwStatusChangeRequests = flwStatusChangeRequestCaptor.getValue();
        assertEquals(1, flwStatusChangeRequests.size());
        assertEquals(Long.valueOf(msisdn), flwStatusChangeRequests.get(0).getMsisdn());
        assertEquals(RegistrationStatus.UNREGISTERED.name(), flwStatusChangeRequests.get(0).getRegistrationStatus());
    }
}
