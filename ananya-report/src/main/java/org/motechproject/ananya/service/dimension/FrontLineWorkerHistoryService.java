package org.motechproject.ananya.service.dimension;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FrontLineWorkerHistoryService {

    @Autowired
    private AllFrontLineWorkerHistory allFrontLineWorkerHistory;

    public FrontLineWorkerHistoryService() {
    }

    public FrontLineWorkerHistoryService(AllFrontLineWorkerHistory allFrontLineWorkerHistory) {
        this.allFrontLineWorkerHistory = allFrontLineWorkerHistory;
    }

    @Transactional
    public void create(RegistrationMeasure registrationMeasure) {
        markCurrentAsOld(registrationMeasure.getFrontLineWorkerDimension());
        allFrontLineWorkerHistory.createOrUpdate(new FrontLineWorkerHistory(registrationMeasure));
    }

    @Transactional
    public void markCurrentAsOld(FrontLineWorkerDimension frontLineWorkerDimension) {
        FrontLineWorkerHistory current = allFrontLineWorkerHistory.getCurrent(frontLineWorkerDimension.getId());
        current.markOld();
        allFrontLineWorkerHistory.createOrUpdate(current);
    }
}
