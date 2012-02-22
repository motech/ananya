package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "registration_measure")
@NamedQuery(name = RegistrationMeasure.FIND_BY_FLW_LOCATION_TIME,
        query = "select r from RegistrationMeasure r where r.frontLineWorkerDimension.id=:flw_id and r.timeDimension.id=:time_id and r.locationDimension.id=:location_id")
public class RegistrationMeasure {

    public static final String FIND_BY_FLW_LOCATION_TIME = "find.by.flw.location.time";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private TimeDimension timeDimension;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDimension locationDimension;
    
    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    public RegistrationMeasure() {
    }

    public RegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension,
                               LocationDimension locationDimension,
                               TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
        this.locationDimension = locationDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }

    public void setTimeDimension(TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public void setFrontLineWorkerDimension(FrontLineWorkerDimension frontLineWorkerDimension) {
        this.frontLineWorkerDimension = frontLineWorkerDimension;
    }
}
