package org.motechproject.ananya.domain.dimension;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "location_dimension")
public class Location {
    @Id
    @Column(name="id")
    private Integer id;
    @Column(name="location_id")
    private String locationId;
    @Column(name = "district")
    private String district;
    @Column(name = "block")
    private String block;
    @Column(name = "panchayat")
    private String panchayat;

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
