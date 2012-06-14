package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.service.helpers.CourseItemMeasureServiceHelper;
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

    private static final Logger log = LoggerFactory.getLogger(CertificateCourseDataHandler.class);
    private CourseItemMeasureService courseItemMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private SMSLogService smsLogService;
    private SendSMSService sendSMSService;

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService,
                                        CallDurationMeasureService callDurationMeasureService,
                                        RegistrationMeasureService registrationMeasureService,
                                        SMSLogService smsLogService,
                                        SendSMSService sendSMSService) {
        this.courseItemMeasureService = courseItemMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.registrationMeasureService = registrationMeasureService;
        this.smsLogService = smsLogService;
        this.sendSMSService = sendSMSService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.CERTIFICATE_COURSE_CALL_MESSAGE})
    public void handleCertificateCourseData(MotechEvent event) {
        for (Object object : event.getParameters().values()) {
            CallMessage callMessage = (CallMessage) object;
            String callId = callMessage.getCallId();
            log.info("Received the certificate course call message for callId: " + callId);

            registrationMeasureService.createRegistrationMeasureForCall(callId);
            callDurationMeasureService.createCallDurationMeasure(callId);
            CourseItemMeasureServiceHelper courseItemMeasureServiceHelper =
                    courseItemMeasureService.getCourseItemMeasureServiceHelper(callId);
            courseItemMeasureService.createCourseItemMeasure(callId, courseItemMeasureServiceHelper);
            courseItemMeasureService.createCourseItemMeasureAudioTracker(callId, courseItemMeasureServiceHelper);
            handleSMS(callId);
        }
    }

    private void handleSMS(String callId) {
        SMSLog smslog = smsLogService.getSMSLogFor(callId);
        if (smslog != null) {
            sendSMSService.buildAndSendSMS(smslog.getCallerId(), smslog.getLocationId(), smslog.getCourseAttempts());
            smsLogService.deleteFor(smslog);
        }
    }
}
