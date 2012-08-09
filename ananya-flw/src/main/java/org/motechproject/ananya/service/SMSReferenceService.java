package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSReferenceService {

    private static Logger log = LoggerFactory.getLogger(SMSReferenceService.class);

    private AllSMSReferences allSMSReferences;

    @Autowired
    public SMSReferenceService(AllSMSReferences allSMSReferences) {
        this.allSMSReferences = allSMSReferences;
    }

    public SMSReference getSMSReferenceNumber(String callerId) {
        return allSMSReferences.findByMsisdn(callerId);
    }

    public void addSMSReferenceNumber(SMSReference smsReference) {
        allSMSReferences.add(smsReference);
        log.info("created SMS reference for:" + smsReference.getMsisdn());
    }

    public void updateSMSReferenceNumber(SMSReference smsReference) {
        allSMSReferences.update(smsReference);
        log.info("updated SMS reference for:" + smsReference.getMsisdn());
    }

    public boolean isSMSSentFor(FrontLineWorker frontLineWorker) {
        SMSReference smsReference = allSMSReferences.findByMsisdn(frontLineWorker.getMsisdn());
        return smsReference != null && smsReference.referenceNumbers(frontLineWorker.currentCourseAttempt()) != null;
    }

}
