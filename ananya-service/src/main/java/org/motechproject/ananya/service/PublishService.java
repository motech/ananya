package org.motechproject.ananya.service;

import org.motechproject.ananya.requests.LogData;

public interface PublishService {
    void publishSMSSent(LogData logData);

    void publishCallDisconnectEvent(String callId);

    void publishNewRegistration(String callerId);
}
