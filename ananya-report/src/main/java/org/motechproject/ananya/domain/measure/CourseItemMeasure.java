package org.motechproject.ananya.domain.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.dimension.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "course_item_measure")
@NamedQueries(value = {
        @NamedQuery(name = CourseItemMeasure.FIND_BY_CALL_ID, query = "select r from CourseItemMeasure r where r.callId=:callId")
})
public class CourseItemMeasure extends Measure {

    public static final String FIND_BY_CALL_ID = "find.by.callId";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private TimeDimension timeDimension;

    @ManyToOne
    @JoinColumn(name = "course_item_id", nullable = false)
    private CourseItemDimension courseItemDimension;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDimension locationDimension;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private LanguageDimension languageDimension;

    @Column(name = "score")
    private Integer score;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "percentage")
    private Integer percentage;

    @Column(name = "event")
    private String event;

    @Column(name = "call_id")
    private String callId;

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }

    public void setTimeDimension(TimeDimension timeDimension) {
        this.timeDimension = timeDimension;
    }

    public CourseItemDimension getCourseItemDimension() {
        return courseItemDimension;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public Integer getScore() {
        return score;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public CourseItemState getEvent() {
        return CourseItemState.valueOf(event);
    }

    public CourseItemMeasure() {
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

    public CourseItemMeasure(TimeDimension timeDimension,
                             CourseItemDimension courseItemDimension,
                             FrontLineWorkerDimension frontLineWorkerDimension,
                             LocationDimension locationDimension,
                             LanguageDimension languageDimension,
                             DateTime timestamp,
                             Integer score,
                             CourseItemState event,
                             String callId) {
        this.timeDimension = timeDimension;
        this.courseItemDimension = courseItemDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.locationDimension = locationDimension;
        this.languageDimension = languageDimension;
        this.timestamp = new Timestamp(timestamp.getMillis());
        this.score = score;
        this.event = String.valueOf(event);
        this.callId = callId;
    }

    public CourseItemMeasure(String callId, TimeDimension timeDimension,
                             CourseItemDimension courseItemDimension,
                             FrontLineWorkerDimension frontLineWorkerDimension,
                             LocationDimension locationDimension,
                             LanguageDimension languageDimension,
                             DateTime timestamp,
                             Integer duration,
                             Integer percentage) {
        this.timeDimension = timeDimension;
        this.courseItemDimension = courseItemDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.locationDimension = locationDimension;
        this.languageDimension = languageDimension;
        this.timestamp = new Timestamp(timestamp.getMillis());
        this.duration = duration;
        this.percentage = percentage;
        this.callId = callId;
    }
}
