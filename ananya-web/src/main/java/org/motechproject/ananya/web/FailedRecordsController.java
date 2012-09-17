package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.service.FailedRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class FailedRecordsController {

    private FailedRecordsService failedRecordsService;

    @Autowired
    public FailedRecordsController(FailedRecordsService failedRecordsService) {
        this.failedRecordsService = failedRecordsService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/failedrecords/process")
    public void processFailedRecords(@PathVariable DateTime recordDate) throws IOException {
        failedRecordsService.processFailedRecords(recordDate);
    }
}
