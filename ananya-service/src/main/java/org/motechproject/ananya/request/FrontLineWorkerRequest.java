package org.motechproject.ananya.request;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.importer.annotation.ColumnName;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

public class FrontLineWorkerRequest implements Serializable {
    private String name;
    private String msisdn;
    private String designation;
    private LocationRequest location = new LocationRequest();
    private Date lastModified;
    private String flwGuid;

    public FrontLineWorkerRequest() {
    }

    public FrontLineWorkerRequest(String msisdn, String name, String designation, LocationRequest location, Date lastModified, String flwGuid) {
        this.name = name;
        this.msisdn = msisdn;
        this.designation = designation;
        this.location = location;
        this.lastModified = lastModified;
        this.flwGuid = flwGuid;
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

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getFlwGuid() {
        return flwGuid;
    }

    public void setFlwGuid(String flwGuid) {
        this.flwGuid = flwGuid;
    }

    @ColumnName(name = "district")
    public void setDistrict(String district) {
        location.setDistrict(district);
    }

    @ColumnName(name = "block")
    public void setBlock(String block) {
        location.setBlock(block);
    }

    @ColumnName(name = "panchayat")
    public void setPanchayat(String panchayat) {
        location.setPanchayat(panchayat);
    }

    public String toCSV() {
        return "\"" + msisdn + "\"" + "," + "\"" + name + "\"" + "," + "\"" + designation + "\"" + "," + "\"" + location.getDistrict() + "\"" + "," + "\"" + location.getBlock() + "\"" + "," + "\"" + location.getPanchayat() + "\"";
    }

    @JsonIgnore
    public boolean isInvalidMsisdn() {
        return StringUtils.length(msisdn)<10 || !StringUtils.isNumeric(msisdn);
    }

    @JsonIgnore
    public boolean isInvalidName() {
        return StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }
}