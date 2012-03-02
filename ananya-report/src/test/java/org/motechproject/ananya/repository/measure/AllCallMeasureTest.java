package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.springframework.dao.DataIntegrityViolationException;

public class AllCallMeasureTest extends SpringIntegrationTest {
    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        template.save(new CallDurationMeasure(null, "callid",23));
    }
}
