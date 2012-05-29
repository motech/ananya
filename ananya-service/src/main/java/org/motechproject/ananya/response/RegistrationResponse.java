package org.motechproject.ananya.response;

public class RegistrationResponse {
    private String frontLineWorkerDetails;
    private String message;

    public RegistrationResponse(String name, String msisdn, String designation, String operator, String district, String block, String panchayat) {
        frontLineWorkerDetails = name + "," + msisdn + "," + designation + "," + operator + "," + district + "," + block + "," + panchayat;
    }

    public RegistrationResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = validationResponse.getMessage();
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
   