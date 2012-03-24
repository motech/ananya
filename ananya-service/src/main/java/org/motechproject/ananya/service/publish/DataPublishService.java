package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.requests.LogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataPublishService implements PublishService {

    public static final String DB = "db";

    private PublishService publishService;

    @Autowired
    public DataPublishService(DbPublishService dbPublishService,
                              QueuePublishService queuePublishService,
                              @Value("#{serviceProperties['publisher.type']}") String publisherType) {

        this.publishService = publisherType.equalsIgnoreCase(DB) ? dbPublishService : queuePublishService;
    }

    @Override
    public void publishSMSSent(LogData logData) {
        publishService.publishSMSSent(logData);
    }

    @Override
    public void publishCallDisconnectEvent(String callId) {
        publishService.publishCallDisconnectEvent(callId);
    }

    @Override
    public void publishNewRegistration(String callerId) {
        publishService.publishNewRegistration(callerId);
    }
}
