package org.motechproject.ananya.dataSources;

import org.motechproject.ananya.dataSources.mappers.LocationDataMapper;
import org.motechproject.ananya.dataSources.reportData.LocationReportData;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.export.annotation.CSVDataSource;
import org.motechproject.export.annotation.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@CSVDataSource(name = "LOCATION")
public class LocationDataSource {

    private LocationDimensionService locationDimensionService;

    @Autowired
    public LocationDataSource(LocationDimensionService locationDimensionService) {
        this.locationDimensionService = locationDimensionService;
    }

    @DataProvider
    public ArrayList<LocationReportData> queryReport(HashMap<String, String> criteria) {
        if(criteria == null)
            criteria = new HashMap<String, String>();
        String district = criteria.get("district");
        String block = criteria.get("block");
        String panchayat = criteria.get("panchayat");

        List<LocationDimension> locationDimensions = locationDimensionService.getFilteredLocations(district, block, panchayat);

        ArrayList<LocationReportData> locationReportDataList = new ArrayList<LocationReportData>();
        LocationDataMapper locationDataMapper = new LocationDataMapper();
        for(LocationDimension locationDimension : locationDimensions) {
            locationReportDataList.add(locationDataMapper.mapFrom(locationDimension));
        }
        return locationReportDataList;
    }
}
