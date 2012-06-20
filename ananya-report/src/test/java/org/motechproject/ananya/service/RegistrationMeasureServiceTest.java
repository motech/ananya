package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Before
    public void setUp() {
        initMocks(this);
        registrationMeasureService = new RegistrationMeasureService(frontLineWorkerService,
                frontLineWorkerDimensionService, allLocationDimensions, allTimeDimensions, allRegistrationMeasures, registrationLogService);
    }

    @Test
    public void shouldCreateRegistrationMeasure() {
        String callerId = "919986574410";
        String callId = "919986574410-12345";
        String operator = "operator";
        String circle = "circle";
        DateTime registeredDate = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegisteredDate(registeredDate);
        LocationDimension locationDimension = new LocationDimension("id", "district", "block", "panchayat");
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), operator, circle, "", "", "");
        TimeDimension timeDimension = new TimeDimension(registeredDate);
        RegistrationLog registrationLog = new RegistrationLog(callId,callerId, operator, circle);

        when(registrationLogService.getRegistrationLogFor(callId)).thenReturn(registrationLog);
        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(false);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);
        when(allLocationDimensions.getFor(anyString())).thenReturn(locationDimension);
        when(frontLineWorkerDimensionService.createOrUpdate(Long.valueOf(callerId), operator, circle, null, null, "UNREGISTERED")).thenReturn(frontLineWorkerDimension);
        when(allTimeDimensions.getFor(registeredDate)).thenReturn(timeDimension);

        registrationMeasureService.createFor(callId);

        ArgumentCaptor<RegistrationMeasure> captor = ArgumentCaptor.forClass(RegistrationMeasure.class);
        verify(allRegistrationMeasures).createOrUpdate(captor.capture());
        verify(registrationLogService).delete(registrationLog);

        RegistrationMeasure registrationMeasure = captor.getValue();
        assertNotNull(registrationMeasure);
        assertEquals(locationDimension, registrationMeasure.getLocationDimension());
        assertEquals(frontLineWorkerDimension, registrationMeasure.getFrontLineWorkerDimension());
        assertEquals(timeDimension, registrationMeasure.getTimeDimension());
    }

    @Test
    public void shouldUpdateExistingRegistrationMeasureIfTheFrontLineWorkerAlreadyExists() {
        int flwId = 123;
        String callerId = "919986574410";
        String operator = "operator";
        String circle = "circle";
        DateTime registeredDate = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegisteredDate(registeredDate);
        LocationDimension oldLocationDimension = new LocationDimension("oldid", "olddistrict", "oldblock", "oldpanchayat");
        LocationDimension newLocationDimension = new LocationDimension("id", "district", "block", "panchayat");
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), operator, circle, "", "", "");
        frontLineWorkerDimension.setId(flwId);
        TimeDimension timeDimension = new TimeDimension(registeredDate);
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, oldLocationDimension, timeDimension, null);

        when(frontLineWorkerDimensionService.exists(Long.parseLong(callerId))).thenReturn(true);
        when(frontLineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);
        when(frontLineWorkerDimensionService.createOrUpdate(Long.valueOf(callerId), operator, circle, null, null, "UNREGISTERED")).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);
        when(allLocationDimensions.getFor(any(String.class))).thenReturn(newLocationDimension);

        registrationMeasureService.createOrUpdateFor(callerId);

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
}
