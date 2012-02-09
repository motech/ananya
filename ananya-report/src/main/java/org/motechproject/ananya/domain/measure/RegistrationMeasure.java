package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "registration_measure")
@NamedQuery(name = RegistrationMeasure.FIND_BY_FLW_LOCATION_TIME,
        query = "select r from RegistrationMeasure r where r.frontLineWorkerDimension.id=:flw_id and r.timeDimension=:time_id and r.locationDimension=:location_id")
public class RegistrationMeasure {

    public static final String FIND_BY_FLW_LOCATION_TIME = "find.by.flw.location.time";
    @Id
    @Column(name = "id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "time_id")
    private TimeDimension timeDimension;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationDimension locationDimension;
    @ManyToOne
    @JoinColumn(name = "flw_id")
    private FrontLineWorkerDimension frontLineWorkerDimension;

    public RegistrationMeasure() {
    }

    public RegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension,
                               LocationDimension locationDimension, TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
        this.locationDimension = locationDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
    }
}
