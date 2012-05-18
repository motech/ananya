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
import org.motechproject.ananya.response.LocationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

        locationDetailsController.create(district, block, panchayat);

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
        locationDetailsController.create(district, block, panchayat);

        locationDetailsController.create(district, block, panchayat);

        List<Location> allLocations = this.allLocations.getAll();
        assertEquals(1, allLocations.size());
        assertEquals(district, allLocations.get(0).getDistrict());
        assertEquals(block, allLocations.get(0).getBlock());
        assertEquals(panchayat, allLocations.get(0).getPanchayat());
    }

    @Test
    public void shouldGetFilteredLocationsList() {
        allLocationDimensions.add(new LocationDimension("1", "D1", "B1", "P1"));
        allLocationDimensions.add(new LocationDimension("2", "D1", "B2", "P2"));
        allLocationDimensions.add(new LocationDimension("3", "D1", "B2", "P3"));
        allLocationDimensions.add(new LocationDimension("4", "D2", "B3", "P5"));
        allLocationDimensions.add(new LocationDimension("5", "D2", "B4", "P4"));
        when(request.getParameter("district")).thenReturn("D1");
        when(request.getParameter("block")).thenReturn("B2");
        when(request.getParameter("panchayat")).thenReturn(null);

        ModelAndView location = locationDetailsController.getLocation(request);

        Map<String, Object> model = location.getModel();
        List<LocationResponse> locations = (List<LocationResponse>) model.get("filteredLocations");
        assertEquals(1, model.size());
        assertEquals(2, locations.size());
    }
}
