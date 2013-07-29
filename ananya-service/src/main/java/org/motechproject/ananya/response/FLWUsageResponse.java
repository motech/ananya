package org.motechproject.ananya.response;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "flw")
public class FLWUsageResponse {

    @XmlElement
    private String name;
    @XmlElement
    private String designation;
    @XmlElement
    private String verificationStatus;
    @XmlElement
    private String registrationStatus;
    @XmlElement
    private String alternateContactNumber;
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

    public FLWUsageResponse() {
    }

    public FLWUsageResponse(String name,
                            String designation,
                            String verificationStatus,
                            String registrationStatus,
                            String alternateContactNumber,
                            LocationResponse location,
                            List<FLWUsageDetail> usageDetails,
                            List<FLWCallDetail> callDetails,
                            FLWBookmark bookmark,
                            List<String> smsReferenceNumbers) {
        this.name = name;
        this.designation = designation;
        this.verificationStatus = verificationStatus;
        this.registrationStatus = registrationStatus;
        this.alternateContactNumber = alternateContactNumber;
        this.location = location;
        this.usageDetails = usageDetails;
        this.callDetails = callDetails;
        this.bookmark = bookmark;
        this.smsReferenceNumbers = smsReferenceNumbers;
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

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }
}
