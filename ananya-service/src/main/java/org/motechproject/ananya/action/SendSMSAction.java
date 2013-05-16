package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.SMSLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSAction implements CourseAction {

    private static Logger log = LoggerFactory.getLogger(SendSMSAction.class);

    private SMSLogService smsLogService;

    @Autowired
    public SendSMSAction(SMSLogService smsLogService) {
        this.smsLogService = smsLogService;
    }

    @Override
    public void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        if (frontLineWorker.courseInProgress() && frontLineWorker.hasPassedTheCourse() && stateRequestList.hasCourseCompletionInteraction()) {
            frontLineWorker.incrementCertificateCourseAttempts();

            String callId = stateRequestList.getCallId();
            SMSLog smsLog = new SMSLog(stateRequestList.getCallId(),
                    frontLineWorker.getMsisdn(),
                    frontLineWorker.getLocationId(),
                    frontLineWorker.currentCourseAttempt(),
                    frontLineWorker.getLanguage());
            smsLogService.add(smsLog);
            log.info(callId + "- course completion sms sent for " + frontLineWorker.getMsisdn());
        }
    }
}
