package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.support.synchroniser.service.SMSService;
import org.motechproject.deliverytools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSSeed {

    private static final Logger log = LoggerFactory.getLogger(SMSSeed.class);

    @Autowired
    private SMSService smsService;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Seed(priority = 1, version = "1.6", comment = "sending sms to frontlineWorker who had completed course but smsLog got purged during migration: Refer bug #177 ")
    public void sendSMSForCourseCompletedFrontlineWorkers() {
        String callerId = "919931004287";
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        if (frontLineWorker == null) {
            log.info("FLW not present to send SMS:" + callerId);
            return;
        }
        smsService.buildAndSendSMS(frontLineWorker.getMsisdn(), frontLineWorker.getLanguage(), frontLineWorker.getLocationId(), 0);
        log.info("Sent SMS for:" + callerId);
    }
}
