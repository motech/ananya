package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Location'")
public class Location extends MotechBaseDataObject {
    
    @JsonProperty
    private String state;
	@JsonProperty
    private String district;
    @JsonProperty
    private String block;
    @JsonProperty
    private String panchayat;
    @JsonProperty
    private int stateCode;
    @JsonProperty
    private int blockCode;
    @JsonProperty
    private int districtCode;
    @JsonProperty
    private int panchayatCode;
    @JsonProperty
    private String externalId;
    @JsonProperty
    private String locationStatus;

    @JsonProperty
    private DateTime lastModifiedTime;

    public Location() {
    }
//new location in java
    public Location(String state, String district, String block, String panchayat, int stateCode, int districtCode, int blockCode, int panchayatCode, LocationStatus locationStatus, DateTime lastModifiedTime) {
        this.locationStatus = locationStatus == null ? null : locationStatus.name();
        this.state = StringUtils.trimToEmpty(state);
        this.district = StringUtils.trimToEmpty(district);
        this.block = StringUtils.trimToEmpty(block);
        this.panchayat = StringUtils.trimToEmpty(panchayat);
        this.stateCode = stateCode;
        this.districtCode = districtCode;
        this.blockCode = blockCode;
        this.panchayatCode = panchayatCode;
        this.lastModifiedTime = lastModifiedTime;
        this.externalId = "S"+ prependZerosInStateCode(stateCode) + "D" + prependZeros(districtCode) + "B" + prependZeros(blockCode) + "V" + prependZeros(panchayatCode);
    }

    public Location(String state, String district, String block, String panchayat) {
    	this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public static Location getDefaultLocation() {
        return new Location("C00" ,"C00", "C00", "", 0, 0, 0, 0, LocationStatus.VALID, null);
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    @JsonIgnore
    public LocationStatus getLocationStatusAsEnum() {
        return LocationStatus.getFor(locationStatus);
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

    public String getState() {
		return state;
	}

	public int getStateCode() {
		return stateCode;
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

    public void setExternalId(String externalId){
    	this.externalId = externalId;
    }
    
    @JsonIgnore
    public boolean isMissingDetails() {
        return StringUtils.isBlank(state) || StringUtils.isBlank(district) || StringUtils.isBlank(block) || StringUtils.isBlank(panchayat);
    }

    private String prependZeros(int code) {
        return String.format("%03d", code);
    }
    
    private String prependZerosInStateCode(int code) {
        return String.format("%02d", code);
    }
    
    public void setLocationStatus(String locationStatus) {
        this.locationStatus = locationStatus;
    }

    public DateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setState(String state) {
		this.state = state;
	}

	public void setLastModifiedTime(DateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;
        if (state != null ? !StringUtils.equalsIgnoreCase(state, location.state) : location.state != null)
            return false;
        if (district != null ? !StringUtils.equalsIgnoreCase(district, location.district) : location.district != null)
            return false;
        if (block != null ? !StringUtils.equalsIgnoreCase(block, location.block) : location.block != null)
            return false;
        if (panchayat != null ? !StringUtils.equalsIgnoreCase(panchayat, location.panchayat) : location.panchayat != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        result = 31 * result + stateCode;
        result = 31 * result + blockCode;
        result = 31 * result + districtCode;
        result = 31 * result + panchayatCode;
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
        		"state='" + state +
                ", district='" + district +
                ", block='" + block +
                ", panchayat='" + panchayat +
                ", locationStatus='" + locationStatus +
                ", externalId='" + externalId +
                '}';
    }

    public void convertToTitleCase() {
    	state = WordUtils.capitalizeFully(state);
        district = WordUtils.capitalizeFully(district);
        block = WordUtils.capitalizeFully(block);
        panchayat = WordUtils.capitalizeFully(panchayat);
    }
}