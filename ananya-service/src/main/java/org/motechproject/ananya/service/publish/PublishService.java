package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;

public interface PublishService {
    void publishDisconnectEvent(String callId, ServiceType serviceType);
}
