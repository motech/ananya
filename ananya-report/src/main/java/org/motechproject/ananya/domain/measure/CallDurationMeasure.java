package org.motechproject.ananya.domain.measure;


import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;

import javax.persistence.*;

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

    @Column(name = "call_id")
    private String callId;

    @Column(name = "duration")
    private int duration;

    @Column(name = "type")
    private String type;

    public CallDurationMeasure() {
    }

    public CallDurationMeasure(FrontLineWorkerDimension frontLineWorkerDimension, String callId, int duration, String type) {
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.callId = callId;
        this.duration = duration;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
