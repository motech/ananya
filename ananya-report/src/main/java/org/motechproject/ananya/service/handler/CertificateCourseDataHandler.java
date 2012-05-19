package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.requests.CallMessage;
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
    private RegistrationLogService registrationLogService;
    private SMSLogService smsLogService;
    private SendSMSService sendSMSService;

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService,
                                        CallDurationMeasureService callDurationMeasureService,
                                        RegistrationMeasureService registrationMeasureService,
                                        RegistrationLogService registrationLogService,
                                        SMSLogService smsLogService,
                                        SendSMSService sendSMSService) {
        this.courseItemMeasureService = courseItemMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.registrationMeasureService = registrationMeasureService;
        this.registrationLogService = registrationLogService;
        this.smsLogService = smsLogService;
        this.sendSMSService = sendSMSService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY})
    public void handleCertificateCourseData(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((CallMessage) log).getCallId();
            String callerId = ((CallMessage) log).getCallerId();
            LOG.info("CallId is: " + callId);
            createRegistrationMeasure(callerId);
            callDurationMeasureService.createCallDurationMeasure(callId);
            courseItemMeasureService.createCourseItemMeasure(callId);
            handleSMS(callId);
        }
    }

    private void handleSMS(String callId) {
        SMSLog smslog = smsLogService.getSMSLogFor(callId);
        if(smslog != null){
            sendSMSService.buildAndSendSMS(
                    smslog.getCallerId(),
                    smslog.getLocationId(),
                    smslog.getCourseAttempts()
            );
            smsLogService.deleteFor(smslog);
        }
    }

    private void createRegistrationMeasure(String callerId) {
        RegistrationLog registrationLog = registrationLogService.getRegistrationLogFor(callerId);
        if(registrationLog != null){
            registrationMeasureService.createRegistrationMeasure(callerId);
            registrationLogService.delete(registrationLog);
        }
    }
}
