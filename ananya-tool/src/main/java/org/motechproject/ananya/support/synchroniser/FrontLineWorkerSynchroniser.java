package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FrontLineWorkerSynchroniser implements Synchroniser {

    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationMeasureService registrationMeasureService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    public FrontLineWorkerSynchroniser(FrontLineWorkerService frontLineWorkerService,
                                       RegistrationMeasureService registrationMeasureService,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.registrationMeasureService = registrationMeasureService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        SynchroniserLog synchroniserLog = new SynchroniserLog("FrontLineWorker");
        List<FrontLineWorker> frontLineWorkers = frontLineWorkerService.findByRegisteredDate(fromDate, toDate);

        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            Long msisdn = frontLineWorker.msisdn();
            try {
                if (allFrontLineWorkerDimensions.fetchFor(msisdn) == null) {
                    LogData logData = new LogData(LogType.REGISTRATION, msisdn.toString());
                    registrationMeasureService.createRegistrationMeasure(logData);
                    synchroniserLog.add(msisdn.toString(), "Success");
                }
            } catch (Exception e) {
                synchroniserLog.add(msisdn.toString(), "Error: " + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.high;
    }

}
