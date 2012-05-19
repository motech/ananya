package org.motechproject.ananya.request;

import java.io.Serializable;

public class FrontLineWorkerRequest implements Serializable {
    private String name;
    private String msisdn;
    private String operator;
    private String designation;
    private LocationRequest location;

    public FrontLineWorkerRequest() {}

    public FrontLineWorkerRequest(String msisdn, String name,  String designation, String operator, LocationRequest location) {
        this.name = name;
        this.msisdn = msisdn;
        this.operator = operator;
        this.designation = designation;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }
}
