package org.motechproject.ananya.domain.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "course_item_measure")
@NamedQueries(value = {
        @NamedQuery(name = CourseItemMeasure.FIND_BY_FLW_AND_EVENT,
                query = "select r from CourseItemMeasure r where r.frontLineWorkerDimension.id=:flw_id and r.event=:event"),
        @NamedQuery(name = CourseItemMeasure.FIND_BY_FLW_AND_COURSE_ITEM_MEASURE_AND_EVENT,
                query = "select r from CourseItemMeasure r where r.frontLineWorkerDimension.id=:flw_id and r.courseItemDimension.id=:course_item_id and r.event=:event")
}
)
public class CourseItemMeasure {

    public static final String FIND_BY_FLW_AND_EVENT = "find.by.flw.and.event";
    public static final String FIND_BY_FLW_AND_COURSE_ITEM_MEASURE_AND_EVENT = "find.by.flw.and.course.item.measure.and.event";

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

    public CourseItemMeasure(TimeDimension timeDimension,
                             CourseItemDimension courseItemDimension,
                             FrontLineWorkerDimension frontLineWorkerDimension,
                             LocationDimension locationDimension,
                             DateTime timestamp,
                             Integer score,
                             CourseItemState event,
                             String callId) {
        this.timeDimension = timeDimension;
        this.courseItemDimension = courseItemDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.locationDimension = locationDimension;
        this.timestamp = new Timestamp(timestamp.getMillis());
        this.score = score;
        this.event = String.valueOf(event);
        this.callId = callId ;
    }

    public CourseItemMeasure(String callId, TimeDimension timeDimension,
                             CourseItemDimension courseItemDimension,
                             FrontLineWorkerDimension frontLineWorkerDimension,
                             LocationDimension locationDimension,
                             DateTime timestamp,
                             Integer duration,
                             Integer percentage) {
        this.timeDimension = timeDimension;
        this.courseItemDimension = courseItemDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.locationDimension = locationDimension;
        this.timestamp = new Timestamp(timestamp.getMillis());
        this.duration = duration;
        this.percentage = percentage;
        this.callId = callId;
    }
}
