package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.measure.CallMeasure;
import org.springframework.dao.DataIntegrityViolationException;

public class AllCallMeasureTest extends SpringIntegrationTest {
    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        template.save(new CallMeasure(null, "callid",23));
    }
}
