package org.motechproject.ananya.request;

public class LocationRequest {
    private String district;
    private String block;
    private String panchayat;

    public LocationRequest(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getDistrict() {
        return district;
    }

    public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }
}
