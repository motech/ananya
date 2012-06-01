package org.motechproject.ananya.response;

public class RegistrationResponse {
    private String frontLineWorkerDetails;
    private String message;

    public RegistrationResponse(String name, String msisdn, String designation, String operator, String circle, String district, String block, String panchayat) {
        frontLineWorkerDetails = name + "," + msisdn + "," + designation + "," + operator + "," + circle + "," + district + "," + block + "," + panchayat;
    }

    public RegistrationResponse withValidationResponse(FLWValidationResponse FLWValidationResponse) {
        this.message = FLWValidationResponse.getMessage();
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        this.message = "Created/Updated FLW record";
        return this;
    }

    public String getFrontLineWorkerDetails() {
        return frontLineWorkerDetails;
    }

    public String getMessage() {
        return message;
    }
}
   