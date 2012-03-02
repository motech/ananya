package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.model.MotechEvent;
import org.springframework.stereotype.Component;

@Component
public class CallDurationHandler {
    private CallDurationMeasureService callDurationMeasureService;

    public CallDurationHandler() {
    }

    public CallDurationHandler(CallDurationMeasureService callDurationMeasureService) {
        this.callDurationMeasureService = callDurationMeasureService;
    }

    public void handleCallDuration(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();
            this.callDurationMeasureService.createCallDurationMeasure(callId);
        }
    }
}
