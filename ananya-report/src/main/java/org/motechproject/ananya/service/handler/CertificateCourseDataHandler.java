package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.*;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateCourseDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateCourseDataHandler.class);
    private CourseItemMeasureService courseItemMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private SMSSentMeasureService smsSentMeasureService;
    private RegistrationLogService registrationLogService;
    private SendSMSLogService sendSMSLogService;

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService,
                                        CallDurationMeasureService callDurationMeasureService, RegistrationMeasureService registrationMeasureService, SMSSentMeasureService smsSentMeasureService, RegistrationLogService registrationLogService, SendSMSLogService sendSMSLogService) {
        this.courseItemMeasureService = courseItemMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.registrationMeasureService = registrationMeasureService;
        this.smsSentMeasureService = smsSentMeasureService;
        this.registrationLogService = registrationLogService;
        this.sendSMSLogService = sendSMSLogService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY})
    public void handleCertificateCourseData(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getCallId();
            String callerId = ((LogData) log).getCallerId();
            LOG.info("CallId is: " + callId);
            createRegistrationMeasure(callerId);
            callDurationMeasureService.createCallDurationMeasure(callId);
            courseItemMeasureService.createCourseItemMeasure(callId);
            createSMSSentMeasure(callerId);
        }
    }

    private void createSMSSentMeasure(String callerId) {
        if(sendSMSLogService.sendSMSLogFor(callerId) != null){
            smsSentMeasureService.createSMSSentMeasure(callerId);
            sendSMSLogService.deleteFor(callerId);
        }
    }

    private void createRegistrationMeasure(String callerId) {
        if(registrationLogService.registrationLogFor(callerId) != null){
            registrationMeasureService.createRegistrationMeasure(callerId);
            registrationLogService.deleteFor(callerId);
        }
    }
}
