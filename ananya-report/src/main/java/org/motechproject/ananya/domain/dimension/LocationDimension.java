package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "location_dimension")
@NamedQuery(name = LocationDimension.FIND_BY_LOCATION_ID, query = "select l from LocationDimension l where l.locationId=:location_id")
public class LocationDimension {

    @Id
    @Column(name = "id")
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

    public LocationDimension(String locationId, String district, String block, String panchayat) {
        this.locationId = locationId;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public Integer getId() {
        return this.id;
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
}
