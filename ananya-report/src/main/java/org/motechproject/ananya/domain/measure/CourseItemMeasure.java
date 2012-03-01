package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "course_item_measure")
public class CourseItemMeasure {

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

    @Column(name = "score")
    private Integer score;

    @Column(name = "event")
    private String event;

    public CourseItemMeasure() {
    }

    public CourseItemMeasure(Integer id, TimeDimension timeDimension, CourseItemDimension courseItemDimension, FrontLineWorkerDimension frontLineWorkerDimension, Integer score, String event) {
        this.id = id;
        this.timeDimension = timeDimension;
        this.courseItemDimension = courseItemDimension;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.score = score;
        this.event = event;
    }
}
