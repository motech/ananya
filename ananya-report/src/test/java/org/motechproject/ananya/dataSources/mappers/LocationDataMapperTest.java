package org.motechproject.ananya.dataSources.mappers;

import org.junit.Test;
import org.motechproject.ananya.dataSources.reportData.LocationReportData;
import org.motechproject.ananya.domain.dimension.LocationDimension;

import static org.junit.Assert.assertEquals;

public class LocationDataMapperTest {
    @Test
    public void shouldMapFromLocationDimension() {
        LocationReportData locationReportData = new LocationDataMapper().mapFrom(new LocationDimension("l1", "s1", "d1", "b1", "p1", "VALID"));

        assertEquals("l1", locationReportData.getLocationId());
        assertEquals("s1", locationReportData.getState());
        assertEquals("d1", locationReportData.getDistrict());
        assertEquals("b1", locationReportData.getBlock());
        assertEquals("p1", locationReportData.getPanchayat());
    }
}
