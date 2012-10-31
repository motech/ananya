package org.motechproject.ananya.dataSources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.dataSources.reportData.LocationReportData;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.export.annotation.CSVDataSource;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationDataSourceTest {
    @Mock
    private LocationDimensionService locationDimensionService;
    private LocationDataSource locationDataSource;

    @Before
    public void setUp() {
        initMocks(this);
        locationDataSource = new LocationDataSource(locationDimensionService);
    }

    @Test
    public void shouldVerifyThatItBelongsToLocationReportGroup() {
        assertEquals("LOCATION", locationDataSource.getClass().getAnnotation(CSVDataSource.class).name());
    }

    @Test
    public void shouldCreateLocationReport() {
        HashMap<String, String> criteria = new HashMap<String, String>();
        ArrayList<LocationDimension> locationDimensions = new ArrayList<LocationDimension>();
        locationDimensions.add(new LocationDimension("locId", "d1", "b1", "p1", "VALID"));
        when(locationDimensionService.getFilteredLocations(null, null, null)).thenReturn(locationDimensions);

        ArrayList<LocationReportData> locationReportDataList = locationDataSource.queryReport(criteria);

        assertEquals(locationReportDataList.size(), 1);
        LocationReportData locationReportData = locationReportDataList.get(0);
        assertEquals("locId", locationReportData.getLocationId());
        assertEquals("d1", locationReportData.getDistrict());
        assertEquals("b1", locationReportData.getBlock());
        assertEquals("p1", locationReportData.getPanchayat());
    }
}
