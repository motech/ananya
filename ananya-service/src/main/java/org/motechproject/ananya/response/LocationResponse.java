package org.motechproject.ananya.response;

public class LocationResponse {
    private String district;
    private String block;
    private String panchayat;

    public LocationResponse(String district, String block, String panchayat) {
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

    @Override
    public String toString() {
        return "{" +
                "\"district\"=\"" + district + "\"" +
                ", \"block\"=\"" + block + "\"" +
                ", \"panchayat\"=\"" + panchayat + "\"" +
                '}';
    }
}
