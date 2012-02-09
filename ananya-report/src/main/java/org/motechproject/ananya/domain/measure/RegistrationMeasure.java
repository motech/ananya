package org.motechproject.ananya.domain.measure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="registration_measure")
public class RegistrationMeasure {

    @Column(name="location_id")
    private Integer locationId;

    @Column(name="time_id")
    private Integer timeId;

    @Column(name="flw_id")
    private Integer flwId;
}
