package org.motechproject.ananya.response;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "flw")
public class FrontLineWorkerUsageResponse {

    @XmlElement
    private String name;
    @XmlElement
    private String designation;
    @XmlElement
    private String registrationStatus;
    @XmlElement
    private LocationResponse location;
    @XmlElementWrapper(name = "usageDetails")
    @XmlElement(name = "usageDetail")
    private List<FLWUsageDetail> usageDetails;
    @XmlElementWrapper(name = "callDetails")
    @XmlElement(name = "callDetail")
    private List<FLWCallDetail> callDetails;
    @XmlElement
    private FLWBookmark bookmark;
    @XmlElementWrapper(name = "smsReferenceNumbers")
    @XmlElement(name = "smsReferenceNumber")
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

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
