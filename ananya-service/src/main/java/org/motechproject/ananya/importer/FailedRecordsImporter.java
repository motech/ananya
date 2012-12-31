package org.motechproject.ananya.importer;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.ananya.validators.FailedRecordsValidator;
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

    private final Logger logger = LoggerFactory.getLogger(FailedRecordsImporter.class);

    private FailedRecordsService failedRecordsService;
    private FailedRecordsValidator failedRecordsValidator;


    @Autowired
    public FailedRecordsImporter(FailedRecordsService failedRecordsService, FailedRecordsValidator failedRecordsValidator) {
        this.failedRecordsService = failedRecordsService;
        this.failedRecordsValidator = failedRecordsValidator;
    }

    @Validate
    public ValidationResponse validate(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        logger.info("Started validating Failed Records");
        ValidationResponse validationResponse = failedRecordsValidator.validate(failedRecordCSVRequests);
        logger.info("Finished validating Failed Records");
        return validationResponse;
    }

    @Post
    public void postData(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        logger.info("Started posting Failed Records");
        failedRecordsService.publishFailedRecordsForProcessing(failedRecordCSVRequests);
        logger.info("Finished posting Failed Records");
    }
}
