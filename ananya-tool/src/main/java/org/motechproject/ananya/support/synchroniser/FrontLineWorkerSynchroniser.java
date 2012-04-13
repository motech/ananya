package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FrontLineWorkerSynchroniser implements Synchroniser {

    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationMeasureService registrationMeasureService;
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    public FrontLineWorkerSynchroniser(FrontLineWorkerService frontLineWorkerService,
                                       RegistrationMeasureService registrationMeasureService,
                                       AllTimeDimensions allTimeDimensions) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.registrationMeasureService = registrationMeasureService;
        this.allTimeDimensions = allTimeDimensions;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        return new SynchroniserLog("FrontLineWorker");

    }

}
