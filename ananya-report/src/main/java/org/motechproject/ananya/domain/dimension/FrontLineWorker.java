package org.motechproject.ananya.domain.dimension;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "frontline_worker")
public class FrontLineWorker {

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
}
