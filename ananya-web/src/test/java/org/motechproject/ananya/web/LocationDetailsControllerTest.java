package org.motechproject.ananya.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationDetailsControllerTest extends SpringIntegrationTest {

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
    @After
    public void setUp() {
        initMocks(this);
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldCreateNewLocationDetail() {
        String panchayat = "Charkot";
        String block = "Amer";
        String district = "Patna";

        locationDetailsController.create(new LocationRequest(district, block, panchayat));

        List<Location> allLocations = this.allLocations.getAll();
        assertEquals(1, allLocations.size());
        assertEquals(district, allLocations.get(0).getDistrict());
        assertEquals(block, allLocations.get(0).getBlock());
        assertEquals(panchayat, allLocations.get(0).getPanchayat());
    }

    @Test
    public void shouldNotDuplicateLocationDetail() {
        String panchayat = "Charkot";
        String block = "Amer";
        String district = "Patna";

        LocationRegistrationResponse response = locationDetailsController.create(new LocationRequest(district, block, panchayat));
        LocationRegistrationResponse response1 = locationDetailsController.create(new LocationRequest(district, block, panchayat));

        List<Location> allLocations = this.allLocations.getAll();
        assertEquals(1, allLocations.size());
        assertEquals(district, allLocations.get(0).getDistrict());
        assertEquals(block, allLocations.get(0).getBlock());
        assertEquals(panchayat, allLocations.get(0).getPanchayat());
        assertEquals("Successfully registered location", response.getMessage());
        assertEquals("[The location is already present]", response1.getMessage());
    }

    @Test
    public void shouldGetFilteredLocationsList() {
        allLocationDimensions.saveOrUpdate(new LocationDimension("1", "D1", "B1", "P1", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("2", "D1", "B2", "P2", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("3", "D1", "B2", "P3", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("4", "D2", "B3", "P5", "VALID"));
        allLocationDimensions.saveOrUpdate(new LocationDimension("5", "D2", "B4", "P4", "VALID"));
        when(request.getParameter("district")).thenReturn("D1");
        when(request.getParameter("block")).thenReturn("B2");
        when(request.getParameter("panchayat")).thenReturn(null);

        List<LocationResponse> locations = locationDetailsController.search(request);

        assertEquals(2, locations.size());
    }
}
