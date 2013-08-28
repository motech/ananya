package org.motechproject.ananya.domain.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "job_aid_content_measure")
@NamedQuery(name = JobAidContentMeasure.FIND_BY_CALL_ID, query = "select m from JobAidContentMeasure m where m.callId = :call_id")
public class JobAidContentMeasure extends Measure{

    public static final String FIND_BY_CALL_ID = "find.by.job.aid.call.id";

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

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private LanguageDimension languageDimension;
    
    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "percentage")
    private Integer percentage;

    public JobAidContentMeasure() {
    }

    public JobAidContentMeasure(String callId, FrontLineWorkerDimension frontLineWorkerDimension,
                                LocationDimension locationDimension, JobAidContentDimension jobAidContentDimension,
                                TimeDimension timeDimension, LanguageDimension languageDimension, DateTime timestamp, Integer duration, Integer percentage) {
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
        this.locationDimension = locationDimension;
        this.jobAidContentDimension = jobAidContentDimension;
        this.languageDimension=languageDimension;
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

    public void setTimeDimension(TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
    }

    public JobAidContentDimension getJobAidContentDimension() {
        return jobAidContentDimension;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }

	public LanguageDimension getLanguageDimension() {
		return languageDimension;
	}

	public void setLanguageDimension(LanguageDimension languageDimension) {
		this.languageDimension = languageDimension;
	}
    
}
