package org.motechproject.ananya.domain.dimension;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "front_line_worker_history")
@NamedQueries({
        @NamedQuery(name = FrontLineWorkerHistory.GET_CURRENT, query = "select history from FrontLineWorkerHistory history where history.flwId=:flwId and history.isCurrent=true"),
})

public class FrontLineWorkerHistory {

    public static final String GET_CURRENT = "current.flw.history";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "flw_id")
    private Integer flwId;

    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "operator")
    private String operator;

    @Column(name = "circle")
    private String circle;

    @Column(name = "name")
    private String name;

    @Column(name = "designation")
    private String designation;

    @Column(name = "status")
    private String status;

    @Column(name = "guid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID guid;

    @Column(name = "verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Column(name = "alternate_contact_number")
    private Long alternateContactNumber;

    @Column(name = "time_id")
    private Integer timeId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "call_id")
    private String callId;

    @Column(name = "is_current")
    private boolean isCurrent;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public FrontLineWorkerHistory() {
    }

    public FrontLineWorkerHistory(RegistrationMeasure registrationMeasure) {
        FrontLineWorkerDimension flw = registrationMeasure.getFrontLineWorkerDimension();
        flwId = flw.getId();
        msisdn = flw.getMsisdn();
        operator = flw.getOperator();
        circle = flw.getCircle();
        name = flw.getName();
        designation = flw.getDesignation();
        status = flw.getStatus();
        guid = flw.getFlwId();
        verificationStatus = flw.getVerificationStatus();
        alternateContactNumber = flw.getAlternateContactNumber();
        timeId = registrationMeasure.getTimeDimension().getId();
        locationId = registrationMeasure.getLocationDimension().getId();
        callId = registrationMeasure.getCallId();
        isCurrent = true;
        timestamp = new Timestamp(DateTime.now().getMillis());
    }

    public void markAsOld() {
        isCurrent = false;
    }

    public Integer id() {
        return id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
