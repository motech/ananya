package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.request.ILogRegistration;
import org.motechproject.ananya.request.LogRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private AllRegistrationLogs allRegistrationLogs;

    @Autowired
    public LogService(AllRegistrationLogs allRegistrationLogs) {
        this.allRegistrationLogs = allRegistrationLogs;
    }

    public void addNew(RegistrationLog registrationLog) {
        allRegistrationLogs.add(registrationLog);
    }

    public String registered(LogRegistrationRequest registrationRequest) {
        RegistrationLog registrationLog = new RegistrationLog(registrationRequest.callerId(), registrationRequest.calledNumber(), DateTime.now(), DateTime.now(), "");
        registrationLog.designation(registrationRequest.designation()).panchayat(registrationRequest.panchayat());
        allRegistrationLogs.add(registrationLog);
        return registrationLog.getId();
    }

    public String registered(ILogRegistration iLogRegistration) {
        RegistrationLog registrationLog = new RegistrationLog(iLogRegistration.callerId(), iLogRegistration.calledNumber(), DateTime.now(), DateTime.now(), "");
        registrationLog.designation(iLogRegistration.designation()).panchayat(iLogRegistration.panchayat());
        allRegistrationLogs.add(registrationLog);
        return registrationLog.getId();
    }
}
