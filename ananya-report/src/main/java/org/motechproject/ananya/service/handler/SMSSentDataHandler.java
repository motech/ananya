package org.motechproject.ananya.service.handler;

import org.apache.log4j.Logger;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSSentDataHandler {

    private static final Logger logger = Logger.getLogger(RegistrationDataHandler.class);
    
    private SMSSentMeasureService smsSentMeasureService;

    @Autowired
    public SMSSentDataHandler(SMSSentMeasureService smsSentMeasureService) {
        this.smsSentMeasureService = smsSentMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY})
    public void handleSMSSent(MotechEvent motechEvent) {
        logger.info("inside handle SMS event");
        
        for (Object log : motechEvent.getParameters().values()) {
            String callerId = ((LogData) log).getDataId();
            
            logger.info("Caller id is : " + callerId);
            
            this.smsSentMeasureService.createSMSSentMeasure(callerId);
        }
    }
}
