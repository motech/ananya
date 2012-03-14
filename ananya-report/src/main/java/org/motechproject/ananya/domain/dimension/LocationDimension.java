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

    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "panchayat")
    private String panchayat;

    public static final String FIND_BY_LOCATION_ID = "find.by.location.id";

    public LocationDimension() {
    }

    public LocationDimension(String locationId) {
        this.locationId = locationId;
    }

    public LocationDimension(String locationId, String district, String block, String panchayat) {
        this.locationId = locationId;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public LocationDimension cloneValues(LocationDimension locationDimension) {
        this.locationId = locationDimension.locationId;
        this.district = locationDimension.district;
        this.block = locationDimension.block;
        this.panchayat = locationDimension.panchayat;
        return this;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationDimension that = (LocationDimension) o;

        if (block != null ? !block.equals(that.block) : that.block != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) return false;
        if (panchayat != null ? !panchayat.equals(that.panchayat) : that.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locationId != null ? locationId.hashCode() : 0;
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }
}
