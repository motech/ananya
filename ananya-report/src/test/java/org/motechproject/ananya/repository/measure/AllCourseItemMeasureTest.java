package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemEvent;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.springframework.dao.DataIntegrityViolationException;

public class AllCourseItemMeasureTest extends SpringIntegrationTest {

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        template.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), null, 2, CourseItemEvent.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenCourseItemDimensionIsNull() {
        template.save(new CourseItemMeasure(null, new CourseItemDimension(), new FrontLineWorkerDimension(), 2, CourseItemEvent.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenTimeDimensionIsNull() {
        template.save(new CourseItemMeasure(new TimeDimension(), null, new FrontLineWorkerDimension(), 2, CourseItemEvent.START));
    }

}
