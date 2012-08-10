package org.motechproject.ananya.dataSources.mappers;

import org.motechproject.ananya.dataSources.reportData.LocationReportData;
import org.motechproject.ananya.domain.dimension.LocationDimension;

public class LocationDataMapper {
    public LocationReportData mapFrom(LocationDimension locationDimension) {
        return new LocationReportData(locationDimension.getLocationId(), locationDimension.getDistrict(), locationDimension.getBlock(), locationDimension.getPanchayat());
    }
}
