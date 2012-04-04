package org.motechproject.ananya.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.service.publish.QueuePublishService;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SMSPublisherService {
    private static final Logger logger = Logger.getLogger(SMSPublisherService.class);
    private DataPublishService dataPublishService;

    @Autowired
    public SMSPublisherService(DataPublishService dataPublishService) {
        this.dataPublishService = dataPublishService;
    }

    public void publishSMSSent(String msisdn){
        LogData logData = new LogData(LogType.SMS_SENT, msisdn);
        logger.info("published SMS sent:" + msisdn);
        dataPublishService.publishSMSSent(logData);
    }
}
