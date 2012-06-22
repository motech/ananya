package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
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
    public void publishDisconnectEvent(String callId, ServiceType serviceType) {
        if (serviceType.equals(ServiceType.JOB_AID))
            publishJobAidContentData(new CallMessage(CallMessageType.JOBAID, callId));
        else
            publishCertificateCourseData(new CallMessage(CallMessageType.CERTIFICATE_COURSE_DATA, callId));
    }

    private void publishCertificateCourseData(CallMessage logData) {
        eventContext.send(ReportPublishEventKeys.CERTIFICATE_COURSE_CALL_MESSAGE, logData);
    }

    private void publishJobAidContentData(CallMessage logData) {
        eventContext.send(ReportPublishEventKeys.JOBAID_CALL_MESSAGE, logData);
    }
}
