package org.motechproject.ananya.handler;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FailedRecordsHandler {

    private FailedRecordsService failedRecordsService;

    @Autowired
    public FailedRecordsHandler(FailedRecordsService failedRecordsService) {
        this.failedRecordsService = failedRecordsService;
    }

    @MotechListener(subjects = FailedRecordsService.FAILED_RECORDS_PUBLISH_MESSAGE)
    public void handleFailedRecords(MotechEvent event) {
        List<FailedRecordCSVRequest> failedRecords = (List<FailedRecordCSVRequest>) event.getParameters().get("0");
        failedRecordsService.processFailedCSVRequests(failedRecords);
    }
}