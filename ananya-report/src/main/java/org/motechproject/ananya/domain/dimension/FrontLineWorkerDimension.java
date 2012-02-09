package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "frontline_worker")
@NamedQuery(name = FrontLineWorkerDimension.FIND_BY_MSISDN, query = "select f from FrontLineWorkerDimension f where f.msisdn=:msisdn")
public class FrontLineWorkerDimension {

    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="msisdn")
    private Long msisdn;

    @Column(name="operator")
    private String operator;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private String status;

    public static final String FIND_BY_MSISDN = "find.by.msisdn";

    public FrontLineWorkerDimension(Long msisdn, String operator, String name, String status) {
        this.msisdn = msisdn;
        this.operator = operator;
        this.name = name;
        this.status = status;
    }

    public Integer getId() {
        return this.id;
    }
}
