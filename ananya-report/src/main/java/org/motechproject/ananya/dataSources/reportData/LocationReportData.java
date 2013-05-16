package org.motechproject.ananya.dataSources.reportData;

import org.motechproject.export.annotation.ExportValue;

public class LocationReportData {

    private final String locationId;
    private final String state;
    private final String district;
    private final String block;
    private final String panchayat;

    public LocationReportData(String locationId, String state, String district, String block, String panchayat) {
        this.locationId = locationId;
        this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    @ExportValue(column = "LocationId", index = 0)
    public String getLocationId() {
        return locationId;
    }

    @ExportValue(column = "State", index = 1)
    public String getState() {
        return state;
    }
    
    @ExportValue(column = "District", index = 1)
    public String getDistrict() {
        return district;
    }

    @ExportValue(column = "Block", index = 2)
    public String getBlock() {
        return block;
    }

    @ExportValue(column = "Panchayat", index = 3)
    public String getPanchayat() {
        return panchayat;
    }
}
