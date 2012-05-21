package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "front_line_worker_dimension")
@NamedQueries({
        @NamedQuery(name = FrontLineWorkerDimension.FIND_BY_MSISDN, query = "select f from FrontLineWorkerDimension f where f.msisdn=:msisdn"),
        @NamedQuery(name = FrontLineWorkerDimension.FIND_ALL_UNREGISTERED, query = "select f from FrontLineWorkerDimension f where f.status='UNREGISTERED'")
})
public class FrontLineWorkerDimension {

    public static final String FIND_BY_MSISDN = "find.by.msisdn";

    public static final String FIND_ALL_UNREGISTERED = "find.all.unregistered";

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="msisdn")
    private Long msisdn;

    @Column(name="operator")
    private String operator;


    @Column(name="circle")
    private String circle;

    @Column(name="name")
    private String name;

    @Column (name="designation")
    private String designation;

    @Column(name="status")
    private String status;

    public FrontLineWorkerDimension() {
    }

    public FrontLineWorkerDimension(Long msisdn, String operator, String circle, String name, String designation, String status) {
        this.msisdn = msisdn;
        this.operator = operator;
        this.name = name;
        this.designation = designation;
        this.status = status;
        this.circle = circle;
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

    public FrontLineWorkerDimension update(String circle, String operator, String name, String status, String designation) {
        this.operator = operator;
        this.name = name;
        this.status = status;
        this.designation = designation;
        this.circle = circle;
        return this;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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
}
