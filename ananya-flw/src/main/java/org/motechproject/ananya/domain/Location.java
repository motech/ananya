package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Location'")
public class Location extends MotechBaseDataObject{

    @JsonProperty
    public String externalId;
    @JsonProperty
    public String district;
    @JsonProperty
    public String blockName;
    @JsonProperty
    public String panchayat;

    public Location() {
    }

    public Location(String externalId, String district, String blockName, String panchayat) {
        this.externalId = externalId;
        this.district = district;
        this.blockName = blockName;
        this.panchayat = panchayat;
    }

    public String getExternalId() {
        return externalId;
    }

    public String district() {
        return district;
    }

    public String blockName() {
        return blockName;
    }

    public String panchayat() {
        return panchayat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (blockName != null ? !blockName.equals(location.blockName) : location.blockName != null) return false;
        if (district != null ? !district.equals(location.district) : location.district != null) return false;
        if (externalId != null ? !externalId.equals(location.externalId) : location.externalId != null) return false;
        if (panchayat != null ? !panchayat.equals(location.panchayat) : location.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = externalId != null ? externalId.hashCode() : 0;
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (blockName != null ? blockName.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }
}
