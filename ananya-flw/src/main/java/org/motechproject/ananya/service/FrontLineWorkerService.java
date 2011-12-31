package org.motechproject.ananya.service;

import org.apache.commons.fileupload.FileItem;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllRecordedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontLineWorkerService {

    public static final String MSISDN_PARAM = "msisdn";

    private AllFrontLineWorkers allFrontLineWorkers;
    private AllRecordedContent allRecordedContent;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, AllRecordedContent allRecordedContent) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allRecordedContent = allRecordedContent;
    }

    public FrontLineWorkerStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker != null ? frontLineWorker.status() : FrontLineWorkerStatus.UNREGISTERED;
    }

    public String createNew(List<FileItem> items) {
        String msisdn = getCallerID(items);
        allRecordedContent.add(msisdn, items);
        return msisdn;
    }

    private String getCallerID(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(MSISDN_PARAM))
                return item.getString();
        return null;
    }
}
