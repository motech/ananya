package org.motechproject.ananya.service;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.context.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReportPublishService {

    private EventContext eventContext;
    private static Logger log = LoggerFactory.getLogger(ReportPublishService.class);


    @Autowired
    public ReportPublishService(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishSMSSent(LogData logData) {
        log.info("Log Data is: " + logData + "for publish key SEND_SMS_SENT_DATA_KEY");
        eventContext.send(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY, logData);
    }

    public void publishCallDisconnectEvent(String callId) {
        log.info("Call Id is: " + callId + "for publish key CERTIFICATE_COURSE_DATA");

        publishCertificateCourseData(new LogData(LogType.CERTIFICATE_COURSE_DATA, callId));
        publishCallDuration(new LogData(LogType.CALL_DURATION, callId));
    }

    public void publishNewRegistration(String callerId) {
        log.info("Caller Id is : " + callerId + " for publish key SEND_REGISTRATION_DATA_KEY");
        eventContext.send(ReportPublishEventKeys.SEND_REGISTRATION_DATA_KEY, new LogData(LogType.REGISTRATION, callerId));
    }

    private void publishCallDuration(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY, logData);
    }

    private void publishCertificateCourseData(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY, logData);
    }

}
