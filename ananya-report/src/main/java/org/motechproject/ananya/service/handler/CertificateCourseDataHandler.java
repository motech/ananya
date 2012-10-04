package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.SMSLogService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.CourseAudioTrackerMeasureService;
import org.motechproject.ananya.service.measure.CourseContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateCourseDataHandler {

    private static final Logger log = LoggerFactory.getLogger(CertificateCourseDataHandler.class);

    private CallDurationMeasureService callDurationMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private CourseContentMeasureService courseContentMeasureService;
    private CourseAudioTrackerMeasureService courseAudioTrackerMeasureService;
    private SMSLogService smsLogService;
    private SendSMSService sendSMSService;

    @Autowired
    public CertificateCourseDataHandler(CallDurationMeasureService callDurationMeasureService,
                                        RegistrationMeasureService registrationMeasureService,
                                        CourseContentMeasureService courseContentMeasureService,
                                        CourseAudioTrackerMeasureService courseAudioTrackerMeasureService,
                                        SMSLogService smsLogService,
                                        SendSMSService sendSMSService) {
        this.callDurationMeasureService = callDurationMeasureService;
        this.registrationMeasureService = registrationMeasureService;
        this.smsLogService = smsLogService;
        this.sendSMSService = sendSMSService;
        this.courseContentMeasureService = courseContentMeasureService;
        this.courseAudioTrackerMeasureService = courseAudioTrackerMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.CERTIFICATE_COURSE_CALL_MESSAGE})
    public void handleCertificateCourseData(MotechEvent event) {

        for (Object object : event.getParameters().values()) {
            if (!(object instanceof CallMessage)) {
                log.info("received unknown object: " + object.toString());
                continue;
            }

            CallMessage callMessage = (CallMessage) object;
            String callId = callMessage.getCallId();
            log.info("received course message: " + callId);

            registrationMeasureService.createFor(callId);
            callDurationMeasureService.createFor(callId);
            courseContentMeasureService.createFor(callId);
            courseAudioTrackerMeasureService.createFor(callId);
            handleSMS(callId);
        }
    }

    private void handleSMS(String callId) {
        SMSLog smslog = smsLogService.getSMSLogFor(callId);
        if (smslog != null)
            sendSMSService.buildAndSendSMS(smslog.getCallerId(), smslog.getLocationId(), smslog.getCourseAttempts());

    }
}
