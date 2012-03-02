package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemEvent;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class AllCourseItemMeasureTest extends SpringIntegrationTest {

    @Autowired
    AllCourseItemMeasures allCourseItemMeasures;

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), null, 2, CourseItemEvent.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenCourseItemDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(null, new CourseItemDimension(), new FrontLineWorkerDimension(), 2, CourseItemEvent.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenTimeDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), null, new FrontLineWorkerDimension(), 2, CourseItemEvent.START));
    }

}
