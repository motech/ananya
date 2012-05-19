package org.motechproject.ananya.response;

public class RegistrationResponse {

    private String message;
    private String frontLineWorkerDetails;

    public RegistrationResponse(String name, String msisdn, String designation, String operator, String district, String block, String panchayat) {
        frontLineWorkerDetails = name + "," + msisdn + "," + designation + "," + operator + "," + district + "," + block + "," + panchayat;

    }

    public RegistrationResponse withInvalidLocationStatus() {
        message = "Invalid Location";
        return this;
    }

    public RegistrationResponse withInvalidCallerId() {
        message = "Invalid CallerId";
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        message = "New FrontlineWorker added";
        return this;
    }

    public String getFrontLineWorkerDetails() {
        return frontLineWorkerDetails;
    }

    public String getMessage() {
        return message;
    }
}
   