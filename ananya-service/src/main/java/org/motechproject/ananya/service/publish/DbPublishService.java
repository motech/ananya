package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.handler.CertificateCourseDataHandler;
import org.motechproject.ananya.service.handler.JobAidDataHandler;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbPublishService implements PublishService {
    private CertificateCourseDataHandler certificateCourseDataHandler;
    private JobAidDataHandler jobAidDataHandler;

    @Autowired
    public DbPublishService(CertificateCourseDataHandler certificateCourseDataHandler, JobAidDataHandler jobAidDataHandler) {
        this.certificateCourseDataHandler = certificateCourseDataHandler;
        this.jobAidDataHandler = jobAidDataHandler;
    }

    @Override
    public void publishCallDisconnectEvent(String callId, String callerId, ServiceType serviceType) {
        MotechEvent motechEvent = new MotechEvent(ReportPublishEventKeys.DB_PUBLISH_KEY);
        motechEvent.getParameters().put("logData", new CallMessage(null, callId, callerId));

        if (serviceType.equals(ServiceType.CERTIFICATE_COURSE))
            certificateCourseDataHandler.handleCertificateCourseData(motechEvent);
        else
            jobAidDataHandler.handleJobAidData(motechEvent);
    }

}
