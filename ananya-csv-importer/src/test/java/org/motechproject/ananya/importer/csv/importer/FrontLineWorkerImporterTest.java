package org.motechproject.ananya.importer.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerImporterTest {

    @Mock
    private LocationService locationService;
    @Mock
    private RegistrationService registrationService;
    @Captor
    private ArgumentCaptor<List<FrontLineWorkerRequest>> captor;

    private FrontLineWorkerImporter frontLineWorkerImporter;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImporter = new FrontLineWorkerImporter(registrationService, locationService);
    }

    @Test
    public void shouldValidateFLWRequests() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1", 1, 1, 1);
        when(locationService.findFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("msisdn,name,designation,district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
        assertEquals("\"1234567890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfFLWDoesNotHaveAllTheDetails() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1", 1, 1, 1);
        when(locationService.findFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1asdf67890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"1asdf67890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"[Invalid msisdn]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateFLWs() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1", 1, 1, 1);
        when(locationService.findFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "anotherName", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(3, validationResponse.getErrors().size());
        assertEquals("\"1234567890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"[Found duplicate FLW with the same MSISDN]\"", validationResponse.getErrors().get(1).getMessage());
        assertEquals("\"1234567890\",\"anotherName\",\"ANM\",\"D1\",\"B1\",\"P1\",\"[Found duplicate FLW with the same MSISDN]\"", validationResponse.getErrors().get(2).getMessage());
    }

    @Test
    public void shouldSaveFLW() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1", 1, 1, 1);
        when(locationService.findFor("D1", "B1", "P1")).thenReturn(location);
        String msisdn = "1234567890";
        frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));

        frontLineWorkerImporter.postData(frontLineWorkerRequests);

        verify(registrationService).registerAllFLWs(captor.capture());
        List<FrontLineWorkerRequest> flwRequests = captor.getValue();
        assertEquals(1, flwRequests.size());
        assertEquals(msisdn, flwRequests.get(0).getMsisdn());
    }

    @Test
    public void shouldAddNoValidationErrorsIfFLWHasAllTheDetails() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1", 1, 1, 1);
        when(locationService.findFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1"), null, "flwGuid"));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertTrue(validationResponse.isValid());
        assertEquals("\"1234567890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"\"", validationResponse.getErrors().get(1).getMessage());
    }
}
