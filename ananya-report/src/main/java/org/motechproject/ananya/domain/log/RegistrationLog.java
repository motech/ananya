package org.motechproject.ananya.domain.log;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "registration_log")
public class RegistrationLog {
    @Id
    @Column(name = "callId")
    private String callId;
    @Column(name = "callerId")
    private String callerId;
    @Column(name = "calledNumber")
    private String calledNumber;
    @Column(name = "startTime")
    private DateTime startTime;
    @Column(name = "endTime")
    private DateTime endTime;
    
    @Column(name = "designation")
    private String designation;
    @Column(name = "district")
    private String district;
    @Column(name = "block")
    private String block;
    @Column(name = "panchayat")
    private String panchayat;

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }


    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }
}
