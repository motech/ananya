package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FLWStatus;
import org.motechproject.ananya.domain.FrontLineWorker;
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

    public void add(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.add(frontLineWorker);
    }

    public FLWStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker.status();
    }
}
