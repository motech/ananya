package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "location_dimension")
@NamedQuery(name = LocationDimension.FIND_BY_LOCATION_ID, query = "select l from LocationDimension l where l.locationId=:location_id")
public class LocationDimension {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "state")
    private String state;
    
    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "panchayat")
    private String panchayat;

    @Column(name = "status")
    private String status;

    public static final String FIND_BY_LOCATION_ID = "find.by.location.id";

    public LocationDimension() {
    }

    public LocationDimension(String locationId, String state, String district, String block, String panchayat, String status) {
        this.locationId = locationId;
        this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.status = status;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLocationId() {
        return locationId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationDimension that = (LocationDimension) o;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (block != null ? !block.equals(that.block) : that.block != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) return false;
        if (panchayat != null ? !panchayat.equals(that.panchayat) : that.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locationId != null ? locationId.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocationDimension{" +
                "locationId='" + locationId +
                ", state='" + state +
                ", district='" + district +
                ", block='" + block +
                ", panchayat='" + panchayat +
                ", status='" + status +
                '}';
    }
}
