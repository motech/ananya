package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSSentDataHandler {
    
    private SMSSentMeasureService smsSentMeasureService;

    @Autowired
    public SMSSentDataHandler(SMSSentMeasureService smsSentMeasureService) {
        this.smsSentMeasureService = smsSentMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY})
    public void handleSMSSent(MotechEvent motechEvent) {
        for (Object log : motechEvent.getParameters().values()) {
            String callerId = ((LogData) log).getDataId();
            this.smsSentMeasureService.createSMSSentMeasure(callerId);
        }
    }
}
