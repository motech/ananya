package org.motechproject.ananya.importer;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@CSVImporter(entity = "FailedRecordCSVRequest", bean = FailedRecordCSVRequest.class)
@Component
public class FailedRecordsImporter {

    private Logger logger = LoggerFactory.getLogger(FailedRecordsImporter.class);

    private FailedRecordsService failedRecordsService;

    @Autowired
    public FailedRecordsImporter(FailedRecordsService failedRecordsService) {
        this.failedRecordsService = failedRecordsService;
    }

    @Validate
    public ValidationResponse validate(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        return new ValidationResponse(true);
    }

    @Post
    public void postData(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        logger.info("Started posting Failed Records");

        failedRecordsService.publishFailedRecordsForProcessing(failedRecordCSVRequests);

        logger.info("Finished posting Failed Records");
    }
}
