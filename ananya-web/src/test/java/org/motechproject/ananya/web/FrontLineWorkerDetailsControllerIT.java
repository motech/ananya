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
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.*;
import org.motechproject.ananya.seed.TimeSeed;
import org.motechproject.ananya.service.FLWDetailsService;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class FrontLineWorkerDetailsControllerIT extends SpringIntegrationTest {
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
    @Mock
    private FLWDetailsService flwDetailsService;

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
    public void shouldCreateFrontLineWorker() {
        LocationRequest locationRequest = new LocationRequest("D1", "B1", "P1");
        locationRegistrationService.addNewLocation(locationRequest);
        String msisdn = "91234545354";
        String designation = Designation.ANM.name();
        String name = "name";

        RegistrationResponse registrationResponse = frontLineWorkerDetailsController.create(new FrontLineWorkerRequest(msisdn, name, designation, locationRequest, null, UUID.randomUUID().toString()));

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.parseLong(msisdn));
        assertNotNull(frontLineWorkerDimension);
        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
        assertEquals(registrationResponse.getMessage(), "Created/Updated FLW record");
    }

    @Test
    public void shouldFilterFrontLineWorkersBasedOnTheGivenCriteria() {
        String msisdn = "1234";
        String status = "REGISTERED";
        String name = "name";
        template.save(new FrontLineWorkerDimension(Long.parseLong(msisdn), "airtel", "bihar", name, Designation.ANM.name(), status, UUID.randomUUID()));
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
    
    @Test
    public void shouldGetFlwUsageXmlResponse() throws Exception {
        ArrayList<FLWUsageDetail> flwUsageDetails = new ArrayList<FLWUsageDetail>(){{
            add(new FLWUsageDetail(2012, 12, 1234L, 1234L));
        }};
        ArrayList<FLWCallDetail> flwCallDetails = new ArrayList<FLWCallDetail>(){{
            add(new FLWCallDetail(CallType.MOBILE_ACADEMY, "12-12-2012 12:12:12", "12-12-2012 12:12:12", 2));
        }};
        ArrayList<String> smsReferenceNumbers = new ArrayList<String>(){{
            add("1234");
        }};
        frontLineWorkerDetailsController = new FrontLineWorkerDetailsController(registrationService, flwDetailsService);
        FrontLineWorkerUsageResponse expectedResponse = new FrontLineWorkerUsageResponse("my_name", "ANM", "unregistered",
                new LocationResponse("my_district", "my_block", "my_panchayat"), flwUsageDetails, flwCallDetails,
                new FLWBookmark(1, 1), smsReferenceNumbers);
        when(flwDetailsService.getUsageData("flwId")).thenReturn(expectedResponse);

        MvcResult result = mockMvc(frontLineWorkerDetailsController)
                .perform(get("/flw/flwId/usage").param("channel", "contact_center").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().type("application/xml"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        FrontLineWorkerUsageResponse actualResponse = TestUtils.fromXml(FrontLineWorkerUsageResponse.class, responseString);
        assertTrue(expectedResponse.equals(actualResponse));
    }

    private void clearAllData() {
        allLocations.removeAll();
        allFrontLineWorkers.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
    }
}
