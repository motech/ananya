package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.service.FailedRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class FailedRecordsController {

    private FailedRecordsService failedRecordsService;

    @Autowired
    public FailedRecordsController(FailedRecordsService failedRecordsService) {
        this.failedRecordsService = failedRecordsService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/failedrecords/process")
    public
    @ResponseBody
    String processFailedRecords(@RequestParam String recordDate) {
        DateTime forRecordDate = DateTime.parse(recordDate, DateTimeFormat.forPattern("dd-MM-yyyy"));
        failedRecordsService.processFailedRecords(forRecordDate);

        return "OK";
    }
}
