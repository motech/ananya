package org.motechproject.ananya.domain.measure;


import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;

import javax.persistence.*;

@Entity
@Table(name = "call_duration_measure")
public class CallDurationMeasure {
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
    //TODO:int or integer? [sush,rahul]
    private Integer duration;

    public CallDurationMeasure() {
    }

    public CallDurationMeasure(FrontLineWorkerDimension frontLineWorkerDimension, String callId, Integer duration) {
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
        this.duration = duration;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public String getCallId() {
        return callId;
    }

    public int getDuration() {
        return duration;
    }
}
