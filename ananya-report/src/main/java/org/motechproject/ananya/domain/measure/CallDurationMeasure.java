package org.motechproject.ananya.domain.measure;


import org.joda.time.DateTime;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "call_duration_measure")
@NamedQuery(name = CallDurationMeasure.FIND_BY_CALL_ID, query = "select cd from CallDurationMeasure cd where cd.callId=:callId")
public class CallDurationMeasure {

    public static final String FIND_BY_CALL_ID = "find.by.call.id";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDimension locationDimension;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private TimeDimension timeDimension;

    @Column(name = "call_id")
    private String callId;

    @Column(name = "duration")
    private int duration;

    @Column(name = "type")
    private String type;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "called_number")
    private Long calledNumber;

    public CallDurationMeasure() {
    }

    public CallDurationMeasure(FrontLineWorkerDimension flwDimension, LocationDimension locationDimension,
                               TimeDimension timeDimension, String callId, Long calledNumber, Integer duration,
                               DateTime startTime, DateTime endTime, String type) {
        this.frontLineWorkerDimension = flwDimension;
        this.locationDimension = locationDimension;
        this.timeDimension = timeDimension;
        this.callId = callId;
        this.calledNumber = calledNumber;
        this.duration = duration;
        this.type = type;
        this.startTime = new Timestamp(startTime.getMillis());
        this.endTime = new Timestamp(endTime.getMillis());
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public String getCallId() {
        return callId;
    }

    public int getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Long getCalledNumber() {
        return calledNumber;
    }

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }
}
