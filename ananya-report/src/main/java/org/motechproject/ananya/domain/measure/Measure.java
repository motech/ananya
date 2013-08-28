package org.motechproject.ananya.domain.measure;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Measure {

    @Column(name = "flw_id", updatable = false, insertable = false)
    private Integer flwId;

    public Integer getFlwId() {
        return flwId;
    }

}
