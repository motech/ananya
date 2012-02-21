package org.motechproject.ananya.domain;

import org.motechproject.ananya.request.ILogRegistration;

import java.util.HashMap;
import java.util.Map;

public class RegistrationRequest extends BaseRequest implements ILogRegistration {
    private String designation;
    private String panchayat;

    public RegistrationRequest(String callerId, String calledNumber, String designation, String panchayat) {
        super(callerId, calledNumber);
        this.designation = designation;
        this.panchayat = panchayat;
    }
    
    public String designation() {
        return this.designation;
    }

    public String panchayat() {
        return this.panchayat;
    }
}
