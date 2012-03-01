package org.motechproject.ananya.domain.measure;


import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;

import javax.persistence.*;

@Entity
@Table(name = "call_measure")
public class CallMeasure {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    @Column(name = "call_id")
    private String callId;

    @Column(name = "duration")
    private Integer duration;

    public CallMeasure() {
    }

    public CallMeasure(Integer id, FrontLineWorkerDimension frontLineWorkerDimension, String callId, Integer duration) {
        this.id = id;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
        this.duration = duration;
    }
}
