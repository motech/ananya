package org.motechproject.ananya.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationResponse {
	
    @XmlElement
    private String state;
    @XmlElement
    private String district;
    @XmlElement
    private String block;
    @XmlElement
    private String panchayat;

    public LocationResponse() {
    }

    public LocationResponse(String state, String district, String block, String panchayat) {
    	this.state = state;
    	this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

	public String getState() {
		return state;
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
        		"\"state\"=\"" + state + "\"" +
                ", \"district\"=\"" + district + "\"" +
                ", \"block\"=\"" + block + "\"" +
                ", \"panchayat\"=\"" + panchayat + "\"" +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
