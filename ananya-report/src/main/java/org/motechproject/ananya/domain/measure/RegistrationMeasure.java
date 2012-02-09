package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "registration_measure")
public class RegistrationMeasure {

    @ManyToOne
    @JoinColumn(name = "time_id")
    private TimeDimension timeDimension;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationDimension locationDimension;
    @ManyToOne
    @JoinColumn(name = "flw_id")
    private FrontLineWorkerDimension frontLineWorkerDimension;

    public RegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension, TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
        this.locationDimension = locationDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
    }
}
