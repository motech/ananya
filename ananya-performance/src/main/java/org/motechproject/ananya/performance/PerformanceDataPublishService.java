package org.motechproject.ananya.performance;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.PublishService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerformanceDataPublishService implements PublishService {

    @Autowired
    private RegistrationMeasureService registrationMeasureService;

    @Override
    public void publishSMSSent(LogData logData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void publishCallDisconnectEvent(String callId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void publishNewRegistration(String callerId) {
        registrationMeasureService.createRegistrationMeasure(new LogData(LogType.REGISTRATION, callerId));
    }
}
