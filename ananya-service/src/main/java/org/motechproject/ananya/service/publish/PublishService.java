package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;

public interface PublishService {
    void publishCallDisconnectEvent(String callId, String callerId, ServiceType serviceType);
}
