package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedRecordsPublishService {

    private EventContext eventContext;

    @Autowired
    public FailedRecordsPublishService(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishFailedRecordsMessage(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        eventContext.send(FailedRecordsService.FAILED_RECORDS_PUBLISH_MESSAGE, failedRecordCSVRequests);
    }
}