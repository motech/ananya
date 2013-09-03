package org.motechproject.ananya.service.dimension;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.TransferableMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang.StringUtils.isBlank;

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
        if (current != null) {
            current.markAsOld();
            allFrontLineWorkerHistory.createOrUpdate(current);
        }
    }

    public void addHistory(TransferableMeasure transferableMeasure) {
        transferableMeasure.addFlwHistory(allFrontLineWorkerHistory.getCurrent(transferableMeasure.flwId()));
    }

    @Transactional
    public void updateOperatorIfNotSet(FrontLineWorkerDimension flw) {
        FrontLineWorkerHistory flwHistory = allFrontLineWorkerHistory.getCurrent(flw.getId());
        if(!isBlank(flwHistory.getOperator())) return;
        flwHistory.setOperator(flw.getOperator());
        allFrontLineWorkerHistory.createOrUpdate(flwHistory);
    }
}
