package org.motechproject.ananya.response;

public class RegistrationResponse {

    private String frontLineWorkerDetails;
    private ValidationResponse validationResponse;

    public RegistrationResponse(String name, String msisdn, String designation, String operator, String district, String block, String panchayat) {
        frontLineWorkerDetails = name + "," + msisdn + "," + designation + "," + operator + "," + district + "," + block + "," + panchayat;
    }

    public RegistrationResponse withValidationResponse(ValidationResponse validationResponse) {
        this.validationResponse = validationResponse;
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        validationResponse = new ValidationResponse();
        return this;
    }

    @Override
    public String toString() {
        String message = validationResponse.getMessage() == null ? "Created/Updated FLW record" : validationResponse.getMessage();
        return "{" +
                "message=\"" + message + '"' +
                ", frontLineWorkerDetails=\"" + frontLineWorkerDetails + '"' +
                '}';
    }
}
   