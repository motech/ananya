package org.motechproject.ananya.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.exceptions.DataAPIException;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.seed.TimeSeed;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerDetailsControllerTest extends SpringIntegrationTest {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private TestDataAccessTemplate template;
    @Autowired
    private FrontLineWorkerDetailsController frontLineWorkerDetailsController;
    @Autowired
    private LocationRegistrationService locationRegistrationService;
    @Autowired
    private AllLocations allLocations;
    @Autowired
    private TimeSeed timeSeed;
    @Mock
    private HttpServletRequest request;
    @Mock
    private RegistrationService registrationService;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        clearAllData();
        timeSeed.createDimensionsInPostgres();
        timeSeed.updateNewlyAddedDateFieldForAllDimensions();
    }

    @After
    public void tearDown() throws IOException {
        clearAllData();
    }

    @Test
    public void shouldCreateFrontLineWorker() throws DataAPIException {
        LocationRequest locationRequest = new LocationRequest("D1", "B1", "P1");
        locationRegistrationService.addNewLocation(locationRequest);
        String msisdn = "91234545354";
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String name = "name";

        RegistrationResponse registrationResponse = frontLineWorkerDetailsController.create(new FrontLineWorkerRequest(msisdn, name, designation, operator, "bihar", locationRequest));

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.parseLong(msisdn));
        assertNotNull(frontLineWorkerDimension);
        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
        assertEquals(registrationResponse.getMessage(), "Created/Updated FLW record");
    }

    @Test
    public void shouldFilterFrontLineWorkersBasedOnTheGivenCriteria() throws DataAPIException {
        String msisdn = "1234";
        String status = "REGISTERED";
        String name = "name";
        template.save(new FrontLineWorkerDimension(Long.parseLong(msisdn), "airtel", "bihar", name, Designation.ANM.name(), status));
        when(request.getParameter("msisdn")).thenReturn(msisdn);
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("status")).thenReturn(status);
        when(request.getParameter("designation")).thenReturn(null);
        when(request.getParameter("operator")).thenReturn(null);
        when(request.getParameter("circle")).thenReturn(null);

        List<FrontLineWorkerResponse> filteredFLWs = frontLineWorkerDetailsController.search(request);

        assertEquals(1, filteredFLWs.size());
        assertEquals(msisdn, filteredFLWs.get(0).getMsisdn());
    }

    @Test(expected = DataAPIException.class)
    public void shouldWrapAllExceptionsDuringCreateInDataAPIException() throws DataAPIException {
        FrontLineWorkerDetailsController flwController = new FrontLineWorkerDetailsController(registrationService);
        when(registrationService.createOrUpdateFLW(any(FrontLineWorkerRequest.class))).thenThrow(new RuntimeException());

        flwController.create(new FrontLineWorkerRequest());
    }

    @Test(expected = DataAPIException.class)
    public void shouldWrapAllExceptionsDuringGetInDataAPIException() throws DataAPIException {
        FrontLineWorkerDetailsController flwController = new FrontLineWorkerDetailsController(registrationService);
        when(registrationService.getFilteredFLW(any(Long.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(Date.class), any(Date.class))).thenThrow(new RuntimeException());

        flwController.search(request);
    }

    private void clearAllData() {
        allLocations.removeAll();
        allFrontLineWorkers.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
    }
}
