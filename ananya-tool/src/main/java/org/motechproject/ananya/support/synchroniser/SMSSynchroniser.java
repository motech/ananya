package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.repository.measure.AllSMSSentMeasures;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SMSSynchroniser implements Synchroniser {

    private AllSMSReferences allSMSReferences;
    private SMSSentMeasureService smsSentMeasureService;
    private AllSMSSentMeasures allSMSSentMeasures;

    @Autowired
    public SMSSynchroniser(AllSMSReferences allSMSReferences, SMSSentMeasureService smsSentMeasureService, AllSMSSentMeasures allSMSSentMeasures) {
        this.allSMSReferences = allSMSReferences;
        this.smsSentMeasureService = smsSentMeasureService;
        this.allSMSSentMeasures = allSMSSentMeasures;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        SynchroniserLog synchroniserLog = new SynchroniserLog("SMS");

        List<SMSReference> smsReferences = allSMSReferences.getAll();
        for (SMSReference smsReference : smsReferences) {
            String callerId = smsReference.getMsisdn();
            try {
                SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(Integer.valueOf(callerId));
                if (smsSentMeasure == null) {
                    smsSentMeasureService.createSMSSentMeasure(callerId);
                    synchroniserLog.add(callerId, "Success");
                }
            } catch (Exception e) {
                synchroniserLog.add(callerId, "Error:" + e.getMessage());
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.medium;
    }
}
