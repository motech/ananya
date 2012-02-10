package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "front_line_worker_dimension")
@NamedQuery(name = FrontLineWorkerDimension.FIND_BY_MSISDN, query = "select f from FrontLineWorkerDimension f where f.msisdn=:msisdn")
public class FrontLineWorkerDimension {

    public static final String FIND_BY_MSISDN = "find.by.msisdn";

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="msisdn")
    private Long msisdn;

    @Column(name="operator")
    private String operator;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private String status;

    public FrontLineWorkerDimension() {
    }

    public FrontLineWorkerDimension(Long msisdn, String operator, String name, String status) {
        this.msisdn = msisdn;
        this.operator = operator;
        this.name = name;
        this.status = status;
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

    public static String getFindByMsisdn() {
        return FIND_BY_MSISDN;
    }

    public Integer getId() {
        return this.id;
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
}
