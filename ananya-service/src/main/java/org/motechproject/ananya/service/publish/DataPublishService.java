package org.motechproject.ananya.service.publish;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataPublishService implements PublishService {

    private static Logger log = LoggerFactory.getLogger(DataPublishService.class);
    
    public static final String DB = "db";

    private PublishService publishService;

    @Autowired
    public DataPublishService(DbPublishService dbPublishService,
                              QueuePublishService queuePublishService,
                              @Value("#{ananyaProperties['publisher.type']}") String publisherType) {

        this.publishService = publisherType.equalsIgnoreCase(DB) ? dbPublishService : queuePublishService;
    }

    @Override
    public void publishSMSSent(LogData logData) {
        try {
            publishService.publishSMSSent(logData);
        }
        catch (Exception e) {
            handlePublishServiceException(e);
        }
    }

    @Override
    public void publishCallDisconnectEvent(String callId, ServiceType serviceType) {
        try {
            publishService.publishCallDisconnectEvent(callId, serviceType);
        }
        catch (Exception e) {
            handlePublishServiceException(e);
        }
    }

    @Override
    public void publishNewRegistration(String callerId) {
        try{
            publishService.publishNewRegistration(callerId);
        }
        catch (Exception e) {
            handlePublishServiceException(e);
        }
    }

    private void handlePublishServiceException(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(ExceptionUtils.getMessage(e));
        sb.append(ExceptionUtils.getStackTrace(e));
        sb.append(ExceptionUtils.getRootCauseMessage(e));
        sb.append(ExceptionUtils.getRootCauseStackTrace(e));
        String exceptionString = sb.toString();
        log.error(exceptionString);
    }
}
