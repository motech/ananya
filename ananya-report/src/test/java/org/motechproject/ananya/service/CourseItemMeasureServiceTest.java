package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.motechproject.ananya.service.dimension.LocationDimensionService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseItemMeasureServiceTest {
    private CourseItemMeasureService courseItemMeasureService;

    @Mock
    private AllCourseItemMeasures allCourseItemMeasures;
    @Mock
    private LocationDimensionService locationDimensionService;
    @Captor
    private ArgumentCaptor<List<CourseItemMeasure>> captor;

    @Before
    public void setUp() {
        courseItemMeasureService = new CourseItemMeasureService(allCourseItemMeasures, locationDimensionService);
    }

    @Test
    public void shouldTransferRecords() {
        FrontLineWorkerDimension fromFlw = new FrontLineWorkerDimension();
        fromFlw.setId(1);
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        toFlw.setId(2);
        courseItemMeasureService.transfer(fromFlw, toFlw);
        verify(allCourseItemMeasures).transfer(CourseItemMeasure.class, 1, 2);
    }

    @Test
    public void shouldUpdateLocationIdForAGivenCallerID() {
        long callerId = 1234L;
        String location_id = "location_id";
        final CourseItemMeasure courseItemMeasure = new CourseItemMeasure();
        ArrayList<CourseItemMeasure> courseItemMeasures = new ArrayList<CourseItemMeasure>() {{
            add(courseItemMeasure);
        }};
        LocationDimension expectedLocationDimension = new LocationDimension();
        when(locationDimensionService.getFor(location_id)).thenReturn(expectedLocationDimension);
        when(allCourseItemMeasures.findByCallerId(callerId)).thenReturn(courseItemMeasures);

        courseItemMeasureService.updateLocation(callerId, location_id);

        verify(allCourseItemMeasures).updateAll(captor.capture());
        List<CourseItemMeasure> actualCourseItemMeasure = captor.getValue();
        assertEquals(expectedLocationDimension, actualCourseItemMeasure.get(0).getLocationDimension());
    }

    @Test
    public void shouldUpdateLocationForAllCourseItemMeasures() {
        String newLocationId = "newLocationId";
        String oldLocationId = "oldLocationId";
        LocationDimension newLocation = new LocationDimension(newLocationId, "S1", "D2", "B2", "P2", "VALID");
        ArrayList<CourseItemMeasure> courseItemMeasures = new ArrayList<>();
        CourseItemMeasure courseItemMeasure = new CourseItemMeasure();
        courseItemMeasure.setLocationDimension(new LocationDimension(oldLocationId, "S1", "D1", "B1", "P1", "VALID"));
        courseItemMeasures.add(courseItemMeasure);
        when(allCourseItemMeasures.findByLocationId(oldLocationId)).thenReturn(courseItemMeasures);
        when(locationDimensionService.getFor(newLocationId)).thenReturn(newLocation);

        courseItemMeasureService.updateLocation(oldLocationId, newLocationId);

        verify(allCourseItemMeasures).updateAll(captor.capture());
        List<CourseItemMeasure> actualCourseItemMeasures = captor.getValue();
        assertEquals(1, actualCourseItemMeasures.size());
        assertEquals(newLocation, actualCourseItemMeasures.get(0).getLocationDimension());
    }
}
