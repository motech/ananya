package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllCourseItemMeasuresTest {
    @Mock
    private DataAccessTemplate template;

    @Test
    public void shouldUpdateAllCourseItemMeasures() {
        AllCourseItemMeasures allCourseItemMeasures = new AllCourseItemMeasures(template);
        ArrayList<CourseItemMeasure> courseItemMeasures = new ArrayList<>();

        allCourseItemMeasures.updateAll(courseItemMeasures);

        verify(template).saveOrUpdateAll(courseItemMeasures);
    }
}
