package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;

import javax.persistence.*;

@Entity
@Table(name = "sms_sent_measure")
@NamedQuery(name = SMSSentMeasure.FIND_SMS_SENT_MEASURE_BY_FLW,
        query = "select r from SMSSentMeasure r where r.frontLineWorkerDimension.id=:flw_id")
public class SMSSentMeasure {
    public static final String FIND_SMS_SENT_MEASURE_BY_FLW = "find.sms.sent.measure.by.flw";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    private FrontLineWorkerDimension frontLineWorkerDimension;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private TimeDimension timeDimension;

    @Column(name = "course_attempt")
    private Integer courseAttempt;

    @Column(name = "sms_sent")
    private Boolean smsSent;

    @Column(name = "sms_reference_number")
    private String smsReferenceNumber;

    public SMSSentMeasure(){
    }

    public SMSSentMeasure(Integer courseAttempt, String smsReferenceNumber, Boolean smsSent, FrontLineWorkerDimension frontLineWorkerDimension, TimeDimension timeDimension) {
        this.courseAttempt = courseAttempt;
        this.smsReferenceNumber = smsReferenceNumber;
        this.smsSent = smsSent;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.timeDimension = timeDimension;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public Integer getCourseAttempt() {
        return courseAttempt;
    }

    public Boolean getSmsSent() {
        return smsSent;
    }

    public String getSmsReferenceNumber() {
        return smsReferenceNumber;
    }

    public TimeDimension getTimeDimension() {
        return timeDimension;
    }
}
