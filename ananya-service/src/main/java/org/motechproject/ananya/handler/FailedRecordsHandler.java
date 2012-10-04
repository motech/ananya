package org.motechproject.ananya.handler;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FailedRecordsHandler {

    private static final Logger log = LoggerFactory.getLogger(FailedRecordsHandler.class);

    private FailedRecordsService failedRecordsService;

    @Autowired
    public FailedRecordsHandler(FailedRecordsService failedRecordsService) {
        this.failedRecordsService = failedRecordsService;
    }

    @MotechListener(subjects = FailedRecordsService.FAILED_RECORDS_PUBLISH_MESSAGE)
    public void handleFailedRecords(MotechEvent event) {
        for (Object object : event.getParameters().values()) {
            if (!((object instanceof List) && !((List) object).isEmpty() && (((List) object).get(0) instanceof FailedRecordCSVRequest))) {
                log.info("received unknown object: " + object.toString());
                continue;
            }

            List<FailedRecordCSVRequest> failedRecords = (List<FailedRecordCSVRequest>) object;
            log.info("Handling failed record requests: " + failedRecords);

            failedRecordsService.processFailedCSVRequests(failedRecords);
        }
    }
}