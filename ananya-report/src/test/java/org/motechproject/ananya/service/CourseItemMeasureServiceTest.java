package org.motechproject.ananya.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

    @Test
    public void shouldUpdateLocationIdForAGivenCallerID() {
        long callerId = 1234L;
        String location_id = "location_id";
        courseItemMeasureService = new CourseItemMeasureService(allCourseItemMeasures, locationDimensionService);
        final CourseItemMeasure courseItemMeasure = new CourseItemMeasure();
        ArrayList<CourseItemMeasure> courseItemMeasures = new ArrayList<CourseItemMeasure>() {{
            add(courseItemMeasure); }};
        LocationDimension expectedLocationDimension = new LocationDimension();
        when(locationDimensionService.getFor(location_id)).thenReturn(expectedLocationDimension);
        when(allCourseItemMeasures.findByCallerId(callerId)).thenReturn(courseItemMeasures);

        courseItemMeasureService.updateLocation(callerId, location_id);

        verify(allCourseItemMeasures).updateAll(captor.capture());
        List<CourseItemMeasure> actualCourseItemMeasure = captor.getValue();
        assertEquals(expectedLocationDimension, actualCourseItemMeasure.get(0).getLocationDimension());
    }
}
