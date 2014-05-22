package org.motechproject.ananya.request;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;

public class LocationRequest implements Serializable {
	
	private String state;
    private String district;
    private String block;
    private String panchayat;

    public LocationRequest() {}

    public LocationRequest(String state, String district, String block, String panchayat) {
    	this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
		return state;
	}

	public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }

	public void setState(String state) {
		this.state = state;
	}
	
    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public String toCSV() {
        return "\"" + state + "\""  + "," + "\"" + district + "\"" + "," + "\"" + block + "\"" + "," + "\"" + panchayat + "\"";
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
