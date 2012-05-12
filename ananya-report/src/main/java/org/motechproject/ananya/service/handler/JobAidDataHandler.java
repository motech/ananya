package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.JobAidContentMeasureService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobAidDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JobAidDataHandler.class);
    private JobAidContentMeasureService jobAidContentMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private RegistrationMeasureService registrationMeasureService;

    @Autowired
    public JobAidDataHandler(JobAidContentMeasureService jobAidContentMeasureService, CallDurationMeasureService callDurationMeasureService, RegistrationMeasureService registrationMeasureService) {
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.registrationMeasureService = registrationMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_JOB_AID_CONTENT_DATA_KEY})
    public void handleJobAidData(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getCallId();
            String callerId =  ((LogData) log).getCallerId();
            LOG.info("CallId is: " + callId);
            registrationMeasureService.createRegistrationMeasure(callerId);
            callDurationMeasureService.createCallDurationMeasure(callId);
            this.jobAidContentMeasureService.createJobAidContentMeasure(callId);
        }
    }
}
