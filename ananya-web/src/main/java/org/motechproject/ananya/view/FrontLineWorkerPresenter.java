package org.motechproject.ananya.view;

public class FrontLineWorkerPresenter {
    private String id;
    private String msisdn;
    private String status;
    private String block;
    private String district;
    private String panchayat;

    public FrontLineWorkerPresenter(String id, String msisdn, String status, String block, String district, String panchayat) {
        this.id = id;
        this.msisdn = msisdn;
        this.status = status;
        this.block = block;
        this.district = district;
        this.panchayat = panchayat;
    }

    public String getId() {
        return id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getStatus() {
        return status;
    }

    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public String getPanchayat() {
        return panchayat;
    }

}
