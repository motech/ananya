package org.motechproject.ananya.action;

import java.util.List;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.contract.CertificateCourseStateRequest;
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
    	log.info("stateRequestList="+stateRequestList+" ");
    	if(stateRequestList!=null){
    		log.info("stateRequestList not null. printing all values="+stateRequestList.toString());
    		log.info(stateRequestList.all().toString());
    		List<CertificateCourseStateRequest> list= stateRequestList.all();
    		 for (CertificateCourseStateRequest stateRequest : list)
    			 log.info(stateRequest.toString());
    			
 
    	}
        if (frontLineWorker.courseInProgress() && frontLineWorker.hasPassedTheCourse() && stateRequestList.hasCourseCompletionInteraction()) {
            frontLineWorker.incrementCertificateCourseAttempts();

            String callId = stateRequestList.getCallId();
            SMSLog smsLog = new SMSLog(stateRequestList.getCallId(),
                    frontLineWorker.getMsisdn(),
                    frontLineWorker.getLocationId(),
                    frontLineWorker.currentCourseAttempts(),
                    frontLineWorker.getLanguage());
            smsLogService.add(smsLog);
            log.info(callId + "- course completion sms sent for " + frontLineWorker.getMsisdn());
        }
    }
}
