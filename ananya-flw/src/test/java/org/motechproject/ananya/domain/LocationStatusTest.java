package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class LocationStatusTest {

    @Test
    public void shouldReturnTheCorrectLocationStatus() {
        assertEquals(LocationStatus.VALID, LocationStatus.getFor(" valiD  "));
        assertEquals(LocationStatus.NOT_VERIFIED, LocationStatus.getFor("  Not_verified"));
        assertNull(LocationStatus.getFor("not verified"));
    }
}
