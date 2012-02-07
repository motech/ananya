package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.ReportData;
import org.motechproject.ananya.domain.log.RegistrationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class ReportDataPublisherIT{

    @Autowired
    private ReportDataPublisher publisher;

    @Test
    @Ignore("should not be run in build cycle, meant for end-to-end testing with queues")
    public void shouldPublishReportDataIntoQueue() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now();

        Map<String, Object> record = new HashMap<String, Object>();
        record.put("callId", "111");
        record.put("callerId", "222");
        record.put("calledNumber", "333");
        record.put("designation", "designation");
        record.put("district", "district");
        record.put("block", "block");
        record.put("panchayat", "panchayat");
        record.put("startTime", startTime);
        record.put("endTime", endTime);
        ReportData reportData = new ReportData(RegistrationLog.class.getName(), record, DateTime.now());

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("1", reportData);

        publisher.publish(reportData);
    }
}
