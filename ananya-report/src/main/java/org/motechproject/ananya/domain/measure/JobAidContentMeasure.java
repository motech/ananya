package org.motechproject.ananya.domain.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "job_aid_content_measure")
public class JobAidContentMeasure {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    @Column(name = "call_id")
    private String callId;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDimension locationDimension;

    @ManyToOne
    @JoinColumn(name = "job_aid_content_id", nullable = false)
    private JobAidContentDimension jobAidContentDimension;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private TimeDimension timeDimension;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "percentage")
    private Integer percentage;

    public JobAidContentMeasure() {
    }

    public JobAidContentMeasure(FrontLineWorkerDimension frontLineWorkerDimension, String callId,
                                LocationDimension locationDimension, JobAidContentDimension jobAidContentDimension,
                                TimeDimension timeDimension, DateTime timestamp, Integer duration, Integer percentage) {
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
        this.locationDimension = locationDimension;
        this.jobAidContentDimension = jobAidContentDimension;
        this.timeDimension = timeDimension;
        this.timestamp = new Timestamp(timestamp.getMillis());
        this.duration = duration;
        this.percentage = percentage;
    }

    public String getCallId() {
        return callId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }

    public JobAidContentDimension getJobAidContentDimension() {
        return jobAidContentDimension;
    }

    public Integer getDuration() {
        return duration;
    }

}
