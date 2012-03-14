package org.motechproject.ananya.service.handler;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDurationHandler {
    private CallDurationMeasureService callDurationMeasureService;
    private static final Logger LOG = Logger.getLogger(CallDurationHandler.class);

    public CallDurationHandler() {
    }

    @Autowired
    public CallDurationHandler(CallDurationMeasureService callDurationMeasureService) {
        this.callDurationMeasureService = callDurationMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY})
    public void handleCallDuration(MotechEvent event) {
        LOG.info("Inside Call Duration Handler.");

        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();

            LOG.info("Call Id is: " + callId);

            this.callDurationMeasureService.createCallDurationMeasure(callId);
        }
    }
}
