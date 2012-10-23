package org.motechproject.ananya.response;


import java.util.List;

public class FrontLineWorkerUsageResponse {
    private String name;
    private String designation;
    private String registrationStatus;
    private LocationResponse location;
    private List<FLWUsageDetail> usageDetails;
    private List<FLWCallDetail> callDetails;
    private FLWBookmark bookmark;
    private List<String> smsReferenceNumbers;

    public FrontLineWorkerUsageResponse() {
    }

    public FrontLineWorkerUsageResponse(String name, String designation, String registrationStatus, LocationResponse location,
                                        List<FLWUsageDetail> usageDetails,
                                        List<FLWCallDetail> callDetails,
                                        FLWBookmark bookmark,
                                        List<String> smsReferenceNumbers) {
        this.name = name;
        this.designation = designation;
        this.registrationStatus = registrationStatus;
        this.location = location;
        this.usageDetails = usageDetails;
        this.callDetails = callDetails;
        this.bookmark = bookmark;
        this.smsReferenceNumbers = smsReferenceNumbers;
    }

    public void withError(List<String> errors) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public LocationResponse getLocation() {
        return location;
    }

    public List<FLWUsageDetail> getUsageDetails() {
        return usageDetails;
    }

    public List<FLWCallDetail> getCallDetails() {
        return callDetails;
    }

    public FLWBookmark getBookmark() {
        return bookmark;
    }

    public List<String> getSmsReferenceNumbers() {
        return smsReferenceNumbers;
    }
}
