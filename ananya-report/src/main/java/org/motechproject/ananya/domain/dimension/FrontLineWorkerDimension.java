package org.motechproject.ananya.domain.dimension;

import org.hibernate.annotations.Type;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "front_line_worker_dimension")
@NamedQueries({
        @NamedQuery(name = FrontLineWorkerDimension.FIND_BY_MSISDN, query = "select f from FrontLineWorkerDimension f where f.msisdn=:msisdn"),
        @NamedQuery(name = FrontLineWorkerDimension.FIND_ALL_UNREGISTERED, query = "select f from FrontLineWorkerDimension f where f.status='UNREGISTERED'")
})
public class FrontLineWorkerDimension {
    public static final String FIND_BY_MSISDN = "find.by.msisdn";
    public static final String FIND_ALL_UNREGISTERED = "find.all.unregistered";
    private final static Logger logger = LoggerFactory.getLogger(FrontLineWorkerDimension.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "alternate_contact_number")
    private Long alternateContactNumber;

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

    @Column(name = "flw_id")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID flwId;

    @Column(name = "verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    public FrontLineWorkerDimension() {
    }

    public FrontLineWorkerDimension(Long msisdn, Long alternateContactNumber, String operator, String circle, String name, String designation, String status, UUID flwId, VerificationStatus verificationStatus) {
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber;
        this.operator = operator;
        this.name = name;
        this.designation = designation;
        this.status = status;
        this.circle = circle;
        this.flwId = flwId;
        this.verificationStatus = verificationStatus;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public String getOperator() {
        return operator;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Integer getId() {
        return this.id;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public FrontLineWorkerDimension update(String circle, Long alternateContactNumber, String operator, String name, String status, String designation, UUID flwId, VerificationStatus verificationStatus) {
        this.operator = operator;
        this.name = name;
        this.status = status;
        this.designation = designation;
        this.circle = circle;
        this.verificationStatus = verificationStatus;
        this.alternateContactNumber = alternateContactNumber;
        updateFlwId(flwId);
        return this;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public UUID getFlwId() {
        return flwId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrontLineWorkerDimension)) return false;

        FrontLineWorkerDimension that = (FrontLineWorkerDimension) o;

        if (circle != null ? !circle.equals(that.circle) : that.circle != null) return false;
        if (designation != null ? !designation.equals(that.designation) : that.designation != null) return false;
        if (!msisdn.equals(that.msisdn)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msisdn.hashCode();
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (circle != null ? circle.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    public void merge(FrontLineWorkerDimension frontLineWorkerDimension) {
        this.name = frontLineWorkerDimension.name;
        this.status = frontLineWorkerDimension.status;
        this.designation = frontLineWorkerDimension.designation;
    }

    public boolean statusIs(RegistrationStatus status) {
        return this.status.equalsIgnoreCase(status.toString());
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    @Override
    public String toString() {
        return "FrontLineWorkerDimension{" +
                "id=" + id +
                ", msisdn=" + msisdn +
                ", operator='" + operator + '\'' +
                ", circle='" + circle + '\'' +
                ", name='" + name + '\'' +
                ", designation='" + designation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    private void updateFlwId(UUID flwId) {
        if (this.flwId != null && !this.flwId.equals(flwId)) {
            logger.warn(String.format("Changing FLWDimension ID for msisdn[%s]", this.msisdn));
        }

        this.flwId = flwId;
    }

    public Long getAlternateContactNumber() {
        return alternateContactNumber;
    }
}
