package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationDetailsControllerIT extends SpringIntegrationTest {

    @Autowired
    private LocationDetailsController locationDetailsController;
    @Autowired
    private AllLocations allLocations;
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private TestDataAccessTemplate template;
    @Mock
    private HttpServletRequest request;
    @Mock
    private LocationRegistrationService locationRegistrationService;

    @Before
    public void setUp() {
        tearDown();
        initMocks(this);
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldGetFilteredLocationsList() {
        allLocationDimensions.saveOrUpdate(new LocationDimension("1", "S1", "D1", "B1", "P1", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("2", "S1", "D1", "B2", "P2", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("3", "S1", "D1", "B2", "P3", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("3", "S2", "D1", "B2", "P3", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("4", "S1", "D2", "B3", "P5", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("5", "S1", "D2", "B4", "P4", "VALID"));
        when(request.getParameter("state")).thenReturn("S1");
        when(request.getParameter("district")).thenReturn("D1");
        when(request.getParameter("block")).thenReturn("B2");
        when(request.getParameter("panchayat")).thenReturn(null);

        List<LocationResponse> locations = locationDetailsController.search(request);

        assertEquals(2, locations.size());
    }

    @Test
    public void shouldCreateNewLocation() {
        LocationRequest locationRequest = new LocationRequest("s", "d", "b", "p");
        LocationSyncRequest locationSyncRequest = new LocationSyncRequest(locationRequest, locationRequest, "VALID", DateTime.now());
        locationDetailsController = new LocationDetailsController(locationRegistrationService);

        locationDetailsController.create(locationSyncRequest);

        verify(locationRegistrationService).addOrUpdate(locationSyncRequest);
    }
}
