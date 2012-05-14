package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationLogService {

    private AllRegistrationLogs allRegistrationLogs;

    @Autowired
    public RegistrationLogService(AllRegistrationLogs allRegistrationLogs) {
        this.allRegistrationLogs = allRegistrationLogs;
    }

    public void add(RegistrationLog registrationLog) {
        allRegistrationLogs.add(registrationLog);
    }

    public RegistrationLog registrationLogFor(String callerId) {
        return allRegistrationLogs.findByCallerId(callerId);
    }

    public void deleteFor(String callerId) {
        allRegistrationLogs.remove(registrationLogFor(callerId));
    }
}