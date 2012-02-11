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

    public void addNew(RegistrationLog registrationLog) {
        allRegistrationLogs.add(registrationLog);
    }
}
