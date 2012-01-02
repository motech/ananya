package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontLineWorkerService {
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public FrontLineWorkerStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker != null ? frontLineWorker.status() : FrontLineWorkerStatus.UNREGISTERED;
    }

    public String createNew(String msisdn) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn);
        allFrontLineWorkers.add(frontLineWorker);
        return msisdn;
    }

}
