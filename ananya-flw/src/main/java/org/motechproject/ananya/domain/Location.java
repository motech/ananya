package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Location'")
public class Location extends MotechBaseDataObject {
    @JsonProperty
    private String district;

    @JsonProperty
    private String block;
    @JsonProperty
    private String panchayat;
    @JsonProperty
    private int blockCode;
    @JsonProperty
    private int districtCode;
    @JsonProperty
    private int panchayatCode;
    @JsonProperty
    private String externalId;

    public Location() {
    }

    public Location(String district, String block, String panchayat, int districtCode, int blockCode, int panchayatCode) {
        this.block = block;
        this.blockCode = blockCode;
        this.district = district;
        this.districtCode = districtCode;
        this.panchayat = panchayat;
        this.panchayatCode = panchayatCode;
        this.externalId = "S01" + "D" + prependZeros(districtCode) + "B" + prependZeros(blockCode) + "V" + prependZeros(panchayatCode);
    }

    public static Location getDefaultLocation() {
        return new Location("C00", "C00", "", 0, 0, 0);
    }

    public int getBlockCode() {
        return blockCode;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public int getPanchayatCode() {
        return panchayatCode;
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

    @JsonIgnore
    public boolean isMissingDetails() {
        String emptyString = "";
        return district.trim().equals(emptyString) || block.trim().equals(emptyString) || panchayat.trim().equals(emptyString);
    }

    private String prependZeros(int code) {
        return String.format("%03d", code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (block != null ? !block.equals(location.block) : location.block != null) return false;
        if (district != null ? !district.equals(location.district) : location.district != null) return false;
        if (panchayat != null ? !panchayat.equals(location.panchayat) : location.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        result = 31 * result + blockCode;
        result = 31 * result + districtCode;
        result = 31 * result + panchayatCode;
        return result;
    }

    public boolean isSameAs(String district, String block, String village) {
        return (StringUtils.equalsIgnoreCase(this.district, district)
                && StringUtils.equalsIgnoreCase(this.block, block)
                && StringUtils.equalsIgnoreCase(this.panchayat, village));
    }
}