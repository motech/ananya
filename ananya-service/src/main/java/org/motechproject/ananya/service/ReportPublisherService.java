package org.motechproject.ananya.service;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReportPublisherService {

    private EventContext eventContext;

    @Autowired
    public ReportPublisherService(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishSMSSent(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY, logData);
    }

    public void publishCallDisconnectEvent(String callId) {
        publishCertificateCourseData(new LogData(LogType.CERTIFICATE_COURSE_DATA, callId));
        publishCallDuration(new LogData(LogType.CALL_DURATION, callId));
    }

    private void publishCallDuration(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY, logData);
    }

    private void publishCertificateCourseData(LogData logData) {
        eventContext.send(ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY, logData);
    }
}
