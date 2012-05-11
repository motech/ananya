package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;
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
public class QueuePublishService implements PublishService {

    private EventContext eventContext;
    private static Logger log = LoggerFactory.getLogger(QueuePublishService.class);


    @Autowired
    public QueuePublishService(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    @Override
    public void publishSMSSent(LogData logData) {
        log.info("Log Data is: " + logData);
        eventContext.send(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY, logData);
    }

    @Override
    public void publishCallDisconnectEvent(String callId, String callerId, ServiceType serviceType) {
        log.info("Call Id is: " + callId);
        if (serviceType.equals(ServiceType.JOB_AID))
            publishJobAidContentData(new LogData(LogType.JOBAID, callId));
        else
            publishCertificateCourseData(new LogData(LogType.CERTIFICATE_COURSE_DATA, callId));

        publishCallDuration(new LogData(LogType.CALL_DURATION, callId));
    }

    public void publishNewRegistration(String callerId) {
        log.info("Caller Id is : " + callerId);
        eventContext.send(ReportPublishEventKeys.SEND_REGISTRATION_DATA_KEY, new LogData(LogType.REGISTRATION, callerId));
    }

    private void publishCallDuration(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY, logData);
    }

    private void publishCertificateCourseData(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY, logData);
    }

    private void publishJobAidContentData(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_JOB_AID_CONTENT_DATA_KEY, logData);
    }
}
