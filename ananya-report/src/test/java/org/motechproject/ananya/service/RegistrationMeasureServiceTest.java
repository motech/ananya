package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.dimension.FrontLineWorkerHistoryService;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationMeasureServiceTest {

    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllLocationDimensions allLocationDimensions;
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private RegistrationLogService registrationLogService;
    @Mock
    private LocationDimensionService locationDimensionService;
    @Mock
    private FrontLineWorkerHistoryService frontLineWorkerHistoryService;
    @Captor
    private ArgumentCaptor<List<RegistrationMeasure>> registrationMeasuresCaptor;
    private int flwId;
    private String callerId;
    private String operator;
    private String circle;
    private String language;
    private DateTime registeredDate;
    private LocationDimension oldLocationDimension;
    private LocationDimension newLocationDimension;
    private FrontLineWorker frontLineWorker;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private TimeDimension timeDimension;
    private RegistrationMeasure registrationMeasure;

    @Before
    public void setUp() {
        initMocks(this);
        registrationMeasureService = new RegistrationMeasureService(frontLineWorkerService,
                frontLineWorkerDimensionService, frontLineWorkerHistoryService, allLocationDimensions,
                allTimeDimensions, allRegistrationMeasures, registrationLogService);
        flwId = 123;
        callerId = "919986574410";
        operator = "operator";
        circle = "circle";
        language = "language";
        registeredDate = DateTime.now();
        oldLocationDimension = new LocationDimension("oldid", "oldstate", "olddistrict", "oldblock", "oldpanchayat", "VALID");
        newLocationDimension = new LocationDimension("id", "state", "district", "block", "panchayat", "VALID");
        frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), null, operator, circle, "", "", "", frontLineWorker.getFlwId(), null);
        timeDimension = new TimeDimension(registeredDate);
        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, oldLocationDimension, timeDimension, null);
    }

    @Test
    public void shouldCreateRegistrationMeasure() {
        String callId = "919986574410-12345";

        DateTime registeredDate = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegisteredDate(registeredDate);
        frontLineWorker.setVerificationStatus(VerificationStatus.OTHER);
        LocationDimension locationDimension = new LocationDimension("id", "state", "district", "block", "panchayat", "VALID");
        TimeDimension timeDimension = new TimeDimension(registeredDate);
        RegistrationLog registrationLog = new RegistrationLog(callId, callerId, operator, circle);

        when(registrationLogService.getRegistrationLogFor(callId)).thenReturn(registrationLog);
        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(false);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);
        when(allLocationDimensions.getFor(anyString())).thenReturn(locationDimension);
        when(frontLineWorkerDimensionService.createOrUpdate(Long.valueOf(callerId), null, operator, circle, null, null, "UNREGISTERED", frontLineWorker.getFlwId(), VerificationStatus.OTHER)).thenReturn(frontLineWorkerDimension);
        when(allTimeDimensions.getFor(registeredDate)).thenReturn(timeDimension);

        registrationMeasureService.createFor(callId);

        verify(frontLineWorkerDimensionService).createOrUpdate(Long.valueOf(callerId), null, operator, circle, null, null, "UNREGISTERED", frontLineWorker.getFlwId(), VerificationStatus.OTHER);
        ArgumentCaptor<RegistrationMeasure> captor = ArgumentCaptor.forClass(RegistrationMeasure.class);
        verify(allRegistrationMeasures).createOrUpdate(captor.capture());
        verify(registrationLogService).delete(registrationLog);

        RegistrationMeasure registrationMeasure = captor.getValue();
        assertNotNull(registrationMeasure);
        assertEquals(locationDimension, registrationMeasure.getLocationDimension());
        assertEquals(frontLineWorkerDimension, registrationMeasure.getFrontLineWorkerDimension());
        assertEquals(timeDimension, registrationMeasure.getTimeDimension());
        verify(frontLineWorkerHistoryService).create(registrationMeasure);
    }

    @Test
    public void shouldUpdateExistingRegistrationMeasureIfTheFrontLineWorkerAlreadyExists() {
        mocksForCreateOrUpdateFor(true);

        registrationMeasureService.createOrUpdateFor(callerId);

        verify(frontLineWorkerDimensionService).createOrUpdate(Long.valueOf(callerId), Long.valueOf(callerId), operator, circle, null, null, "UNREGISTERED", frontLineWorker.getFlwId(), null);
        verify(allRegistrationMeasures).createOrUpdate(registrationMeasure);
        assertEquals(newLocationDimension, registrationMeasure.getLocationDimension());
    }

    @Test
    public void shouldNotRunWhenThereIsNoRegistrationLog() {
        String callerId = "919986574410";
        when(registrationLogService.getRegistrationLogFor(callerId)).thenReturn(null);

        registrationMeasureService.createFor(callerId);

        verify(frontLineWorkerService, never()).findByCallerId(callerId);
    }

    @Test
    public void shouldCreateRegistrationMeasureWithoutLog() {
        String callId = "919986574410-12312312";

        DateTime registeredDate = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegisteredDate(registeredDate);
        LocationDimension locationDimension = new LocationDimension("id", "state", "district", "block", "panchayat", LocationStatus.VALID.name());
        TimeDimension timeDimension = new TimeDimension(registeredDate);
        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(false);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);
        when(allLocationDimensions.getFor(anyString())).thenReturn(locationDimension);
        when(frontLineWorkerDimensionService.createOrUpdate(Long.valueOf(callerId), null, operator, circle, null, null, "UNREGISTERED", UUID.fromString("11111111-1111-1111-1111-111111111111"), null)).thenReturn(frontLineWorkerDimension);
        when(allTimeDimensions.getFor(registeredDate)).thenReturn(timeDimension);

        registrationMeasureService.createRegistrationMeasure(callerId, callId);

        ArgumentCaptor<RegistrationMeasure> captor = ArgumentCaptor.forClass(RegistrationMeasure.class);
        verify(allRegistrationMeasures).createOrUpdate(captor.capture());
        RegistrationMeasure registrationMeasure = captor.getValue();
        assertNotNull(registrationMeasure);
        assertEquals(locationDimension, registrationMeasure.getLocationDimension());
        assertEquals(frontLineWorkerDimension, registrationMeasure.getFrontLineWorkerDimension());
        assertEquals(timeDimension, registrationMeasure.getTimeDimension());
        verify(frontLineWorkerHistoryService).create(registrationMeasure);
    }

    @Test
    public void shouldNotCreateRegistrationMeasureWithoutLogIfItExists() {
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(true);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);

        registrationMeasureService.createRegistrationMeasure(callerId, "919986574410-12312312");

        verify(allRegistrationMeasures, never()).createOrUpdate(any(RegistrationMeasure.class));
        verify(frontLineWorkerHistoryService, never()).create(registrationMeasure);
    }

    @Test
    public void shouldUpdateLocation() {
        String oldLocationId = "oldLocationId";
        ArrayList<RegistrationMeasure> registrationMeasures = new ArrayList<>();
        registrationMeasures.add(new RegistrationMeasure(null, new LocationDimension(oldLocationId, null, null, null, null, "VALID"), null, null));
        when(allRegistrationMeasures.findByLocationId(oldLocationId)).thenReturn(registrationMeasures);
        String newLocationId = "newLocationId";
        when(allLocationDimensions.getFor(newLocationId)).thenReturn(new LocationDimension(newLocationId, null, null, null, null, "VALID"));

        registrationMeasureService.updateLocation(oldLocationId, newLocationId);

        verify(allRegistrationMeasures).updateAll(registrationMeasuresCaptor.capture());
        List<RegistrationMeasure> actualRegistrationMeauresSavedToDb = registrationMeasuresCaptor.getValue();
        assertEquals(1, actualRegistrationMeauresSavedToDb.size());
        assertEquals(newLocationId, actualRegistrationMeauresSavedToDb.get(0).getLocationDimension().getLocationId());
    }

    @Test
    public void shouldRemoveRegistrationMeasure() {
        int flwId = 1;
        RegistrationMeasure registrationMeasure = new RegistrationMeasure();
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);

        registrationMeasureService.remove(flwId);
        verify(allRegistrationMeasures).fetchFor(flwId);
        verify(allRegistrationMeasures).remove(registrationMeasure);
    }

    @Test
    public void shouldNotCreateFlwHistoryOnUpdateFlw() {
        mocksForCreateOrUpdateFor(true);
        RegistrationMeasure registrationMeasure = registrationMeasureService.createOrUpdateFor(callerId);
        verify(frontLineWorkerHistoryService, never()).create(registrationMeasure);
    }

    @Test
    public void shouldCreateFlwHistoryOnCreateFlw() {
        mocksForCreateOrUpdateFor(false);
        when(allTimeDimensions.getFor(any(DateTime.class))).thenReturn(new TimeDimension());
        RegistrationMeasure registrationMeasure = registrationMeasureService.createOrUpdateFor(callerId);
        verify(frontLineWorkerHistoryService).create(registrationMeasure);
    }

    private void mocksForCreateOrUpdateFor(boolean flwDimensionExists) {
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegisteredDate(registeredDate);
        frontLineWorker.setAlternateContactNumber(callerId);
        frontLineWorkerDimension.setId(flwId);

        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(flwDimensionExists);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);
        when(frontLineWorkerDimensionService.createOrUpdate(Long.valueOf(callerId), Long.valueOf(callerId), operator, circle, null, null, "UNREGISTERED", frontLineWorker.getFlwId(), null)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);
        when(allLocationDimensions.getFor(any(String.class))).thenReturn(newLocationDimension);
    }

}
