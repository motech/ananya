package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.measure.TransferableMeasure;
import org.motechproject.ananya.service.dimension.FrontLineWorkerHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class TransferableMeasureService {

    @Autowired
    private FrontLineWorkerHistoryService frontLineWorkerHistoryService;

    protected TransferableMeasureService() {
    }

    public void addFlwHistory(TransferableMeasure transferableMeasure) {
        frontLineWorkerHistoryService.addHistory(transferableMeasure);
    }

}
