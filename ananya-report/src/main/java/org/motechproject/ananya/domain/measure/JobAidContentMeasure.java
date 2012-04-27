package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;

import javax.persistence.*;
import java.util.Date;

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
    private Integer callId;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDimension locationDimension;

    @ManyToOne
    @JoinColumn(name = "job_aid_content_id", nullable = false)
    private JobAidContentDimension jobAidContentDimension;

    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "percentage")
    private Integer percentage;

    public JobAidContentMeasure() {
    }
}
