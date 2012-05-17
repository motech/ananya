package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public RegistrationLog getRegistrationLogFor(String callerId) {
        return allRegistrationLogs.findByCallerId(callerId);
    }

    public void deleteFor(String callerId) {
        allRegistrationLogs.remove(getRegistrationLogFor(callerId));
    }

    public void delete(RegistrationLog registrationLog) {
        allRegistrationLogs.remove(registrationLog);
    }

    public List<RegistrationLog> getAll() {
        return allRegistrationLogs.getAll();
    }
}