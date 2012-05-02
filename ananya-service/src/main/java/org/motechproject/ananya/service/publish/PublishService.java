package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.LogData;

public interface PublishService {
    void publishSMSSent(LogData logData);

    void publishCallDisconnectEvent(String callId, ServiceType serviceType);

    void publishNewRegistration(String callerId);
}
