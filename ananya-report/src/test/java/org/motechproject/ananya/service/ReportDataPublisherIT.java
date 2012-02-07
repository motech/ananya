package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.common.ReportBaseIT;
import org.motechproject.ananya.domain.ReportData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ReportDataPublisherIT extends ReportBaseIT {

    @Autowired
    private ReportDataPublisher publisher;

    @Test
    @Ignore("should not be run in build cycle, meant for end-to-end testing with queues")
    public void shouldPublishReportDataIntoQueue() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now();

        Map<String, Object> callDetail = new HashMap<String, Object>();
        callDetail.put("callId", "111");
        callDetail.put("callerId", "222");
        callDetail.put("calledNumber", "333");
        callDetail.put("startTime", startTime);
        callDetail.put("endTime", endTime);
        ReportData reportData = new ReportData("table", callDetail, DateTime.now());

        publisher.publish(reportData);
    }
}
