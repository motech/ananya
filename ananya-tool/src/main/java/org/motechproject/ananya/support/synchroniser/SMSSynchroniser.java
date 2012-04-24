package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.measure.AllSMSSentMeasures;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.motechproject.ananya.support.synchroniser.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SMSSynchroniser implements Synchroniser {

    private AllSMSReferences allSMSReferences;
    private SMSSentMeasureService smsSentMeasureService;
    private AllSMSSentMeasures allSMSSentMeasures;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;


    @Autowired
    public SMSSynchroniser(AllSMSReferences allSMSReferences,
                           SMSSentMeasureService smsSentMeasureService,
                           AllSMSSentMeasures allSMSSentMeasures,
                           AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.allSMSReferences = allSMSReferences;
        this.smsSentMeasureService = smsSentMeasureService;
        this.allSMSSentMeasures = allSMSSentMeasures;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        SynchroniserLog synchroniserLog = new SynchroniserLog("SMS");
        List<SMSReference> smsReferences = allSMSReferences.getAll();
        
        for (SMSReference smsReference : smsReferences) {
            String callerId = smsReference.getMsisdn();
            try {
                FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
                SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorkerDimension.getId());
                if (smsSentMeasure == null) {
                    smsSentMeasureService.createSMSSentMeasure(callerId);
                    synchroniserLog.add(callerId, "Success");
                }
            } catch (Exception e) {
                synchroniserLog.add(callerId, "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.medium;
    }
}
