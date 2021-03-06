package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "registration_measure")
@NamedQueries(value = {
        @NamedQuery(name = RegistrationMeasure.FIND_BY_FLW_LOCATION_TIME,
                query = "select r from RegistrationMeasure r where r.frontLineWorkerDimension.id=:flw_id " +
                        "and r.timeDimension.id=:time_id and r.locationDimension.id=:location_id"),
        @NamedQuery(name = RegistrationMeasure.FIND_BY_FLW,
                query = "select r from RegistrationMeasure r where r.frontLineWorkerDimension.id=:flw_id")
}
)

public class RegistrationMeasure {

    public static final String FIND_BY_FLW = "find.by.flw";

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

    @Column(name="call_id")
    private String callId;
    
    public RegistrationMeasure() {
    }

    public RegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension,
                               LocationDimension locationDimension,
                               TimeDimension timeDimension,
                               String callId) {
        this.timeDimension = timeDimension;
        this.locationDimension = locationDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
    }

    public Integer getId() {
        return id;
    }

    public String getCallId() {
        return callId;
    }

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public void merge(RegistrationMeasure registrationMeasure) {
        this.timeDimension = registrationMeasure.timeDimension;
        this.locationDimension = registrationMeasure.locationDimension;
    }

    public RegistrationMeasure update(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
        return this;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }

    @Override
    public String toString() {
        return "RegistrationMeasure{" +
                "id=" + id +
                ", timeDimension=" + timeDimension +
                ", locationDimension=" + locationDimension +
                ", frontLineWorkerDimension=" + frontLineWorkerDimension +
                '}';
    }
}
