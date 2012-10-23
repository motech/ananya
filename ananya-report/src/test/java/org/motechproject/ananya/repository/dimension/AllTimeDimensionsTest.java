package org.motechproject.ananya.repository.dimension;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AllTimeDimensionsTest extends SpringIntegrationTest{
    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(TimeDimension.class));
    }

    @Test
    public void shouldGetTimeDimensionAccordingToLocalTimezone() {
        DateTime dateTime = DateTime.parse("2012-10-23");
        allTimeDimensions.makeFor(DateTime.parse("2012-10-22").toDateTime(DateTimeZone.getDefault()));
        allTimeDimensions.makeFor(dateTime.toDateTime(DateTimeZone.getDefault()));
        allTimeDimensions.makeFor(DateTime.parse("2012-10-24").toDateTime(DateTimeZone.getDefault()));

        TimeDimension timeDimension = allTimeDimensions.getFor(dateTime.withTime(3, 30, 0, 0).toDateTime(DateTimeZone.UTC));

        assertEquals(dateTime.getDayOfYear(), (int) timeDimension.getDay());
    }
}
