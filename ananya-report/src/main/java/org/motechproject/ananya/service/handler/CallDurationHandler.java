package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.ReportDataPublisher;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDurationHandler {
    private CallDurationMeasureService callDurationMeasureService;

    public CallDurationHandler() {
    }

    @Autowired
    public CallDurationHandler(CallDurationMeasureService callDurationMeasureService) {
        this.callDurationMeasureService = callDurationMeasureService;
    }

    @MotechListener(subjects = {ReportDataPublisher.SEND_CALL_DURATION_DATA_KEY})
    public void handleCallDuration(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();
            this.callDurationMeasureService.createCallDurationMeasure(callId);
        }
    }
}
