package org.motechproject.ananya.request;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.importer.annotation.ColumnName;

import java.io.Serializable;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FrontLineWorkerRequest implements Serializable {
    private String name;
    private String msisdn;
    private String language;
    private String designation;
    private LocationRequest location = new LocationRequest();
    private DateTime lastModified;
    private String flwId;
    private String verificationStatus;
    private String alternateContactNumber;

    public String getNewMsisdn() {
        return newMsisdn;
    }

    public void setNewMsisdn(String newMsisdn) {
        this.newMsisdn = newMsisdn;
    }

    private String newMsisdn;

    public FrontLineWorkerRequest() {
    }

    public FrontLineWorkerRequest(String msisdn, String alternateContactNumber, String name, String designation, LocationRequest location, DateTime lastModified, String flwId, String verificationStatus, String language, String newMsisdn) {
        this.name = name;
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber;
        this.designation = designation;
        this.location = location;
        this.lastModified = lastModified;
        this.flwId = flwId;
        this.verificationStatus = verificationStatus;
        this.language = language;
        this.newMsisdn = newMsisdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public Long msisdn() {
        return Long.valueOf(msisdn);
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }

    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getFlwId() {
        return flwId;
    }

    public void setFlwId(String flwId) {
        this.flwId = flwId;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public VerificationStatus getVerificationStatusAsEnum() {
        return VerificationStatus.findFor(verificationStatus);
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getLanguage() {
        return language;
    }

    @ColumnName(name = "language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @ColumnName(name = "district")
    public void setDistrict(String district) {
        location.setDistrict(district);
    }

    @ColumnName(name = "block")
    public void setBlock(String block) {
        location.setBlock(block);
    }

    @ColumnName(name = "state")
    public void setState(String state) {
        location.setState(state);
    }

    @ColumnName(name = "panchayat")
    public void setPanchayat(String panchayat) {
        location.setPanchayat(panchayat);
    }

    public String toCSV() {
        return "\"" + msisdn + "\"" + "," + "\"" + name + "\"" + "," + "\"" + language + "\"" + "," + "\"" + designation + "\"" + "," + "\"" + location.getState() + "\"" + "," + "\"" + location.getDistrict() + "\"" + "," + "\"" + location.getBlock() + "\"" + "," + "\"" + location.getPanchayat() + "\"";
    }

    @JsonIgnore
    public boolean isInvalidMsisdn() {
        return invalidFormat(msisdn);
    }

    private boolean invalidFormat(String msisdn) {
        return StringUtils.length(msisdn) < 10 || !StringUtils.isNumeric(msisdn);
    }

    @JsonIgnore
    public boolean isInvalidAlternateContactNumber() {
        return isNotBlank(alternateContactNumber) && invalidFormat(alternateContactNumber);
    }

    @JsonIgnore
    public boolean isInvalidName() {
        return StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }
    @JsonIgnore
    public boolean isInvalidNewMsisdn() {
        if(isBlank(newMsisdn)) return false;
        return invalidFormat(newMsisdn);
    }

    public boolean hasMsisdnChange() {
        return isNotBlank(newMsisdn);
    }
}
