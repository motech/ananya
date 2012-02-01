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
}
