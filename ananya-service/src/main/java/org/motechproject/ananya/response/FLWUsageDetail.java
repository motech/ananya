package org.motechproject.ananya.response;

public class FLWUsageDetail {

    private Integer year;
    private Integer month;
    private Long mobileKunji;
    private Long mobileAcademy;

    public FLWUsageDetail(Integer year, Integer month, Long mobileKunji, Long mobileAcademy) {
        this.year = year;
        this.month = month;
        this.mobileKunji = mobileKunji;
        this.mobileAcademy = mobileAcademy;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Long getMobileKunji() {
        return mobileKunji;
    }

    public Long getMobileAcademy() {
        return mobileAcademy;
    }
}
