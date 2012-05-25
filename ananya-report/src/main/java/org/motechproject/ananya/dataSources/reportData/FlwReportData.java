package org.motechproject.ananya.dataSources.reportData;

import org.motechproject.export.annotation.ReportValue;

public class FlwReportData {
    Long msisdn;
    String name;
    String status;
    String designation;
    String operator;
    String circle;

    public FlwReportData(Long msisdn, String name, String status, String designation, String operator, String circle) {
        this.msisdn = msisdn;
        this.name = name;
        this.status = status;
        this.designation = designation;
        this.operator = operator;
        this.circle = circle;
    }

    @ReportValue(column = "MSISDN", index = 0)
    public String getMsisdn() {
        return msisdn.toString();
    }

    @ReportValue(column = "Name",index = 1)
    public String getName() {
        return name;
    }

    @ReportValue(column = "Status",index = 2)
    public String getStatus() {
        return status;
    }

    @ReportValue(column = "Designation",index = 3)
    public String getDesignation() {
        return designation;
    }

    @ReportValue(column = "Operator",index = 4)
    public String getOperator() {
        return operator;
    }

    @ReportValue(column = "Circle",index = 5)
    public String getCircle() {
        return circle;
    }
}
