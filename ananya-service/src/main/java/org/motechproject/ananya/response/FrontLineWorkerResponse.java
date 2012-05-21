package org.motechproject.ananya.response;

public class FrontLineWorkerResponse {
    String msisdn;
    String name;
    String status;
    String designation;
    String operator;
    String circle;

    public FrontLineWorkerResponse(String msisdn, String name, String status, String designation, String operator, String circle) {
        this.msisdn = msisdn;
        this.name = name;
        this.status = status;
        this.designation = designation;
        this.operator = operator;
        this.circle = circle;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDesignation() {
        return designation;
    }

    public String getOperator() {
        return operator;
    }

    public String getCircle() {
        return circle;
    }

    @Override
    public String toString() {
        return "{" +
                "msisdn='" + msisdn + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", designation='" + designation + '\'' +
                ", operator='" + operator + '\'' +
                ", circle='" + circle + '\'' +
                '}';
    }
}
