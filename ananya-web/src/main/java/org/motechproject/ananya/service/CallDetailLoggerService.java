package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.repository.AllCallDetailLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallDetailLoggerService {
    private AllCallDetailLogs allCallDetailLogs;

    @Autowired
    public CallDetailLoggerService(AllCallDetailLogs allCallDetailLogs) {
        this.allCallDetailLogs = allCallDetailLogs;
    }


    public void Save(CallDetailLog callDetailLog) {
        allCallDetailLogs.addIfAbsent(callDetailLog);
    }

}
