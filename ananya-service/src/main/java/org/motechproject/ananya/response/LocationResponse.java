package org.motechproject.ananya.response;

public class LocationResponse {
    private String district;
    private String block;
    private String panchayat;
    private String externalId;

    public LocationResponse(String district, String block, String panchayat, String externalId) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.externalId = externalId;
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

    public String getExternalId() {
        return externalId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"district\"=\"" + district + "\"" +
                ", \"block\"=\"" + block + "\"" +
                ", \"panchayat\"=\"" + panchayat + "\"" +
                ", \"externalId\"=\"" + externalId + "\"" +
                '}';
    }
}
