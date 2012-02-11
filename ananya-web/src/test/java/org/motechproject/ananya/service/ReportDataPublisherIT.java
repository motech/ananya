package org.motechproject.ananya.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.domain.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class ReportDataPublisherIT {

    @Autowired
    private ReportDataPublisher publisher;

    @Test
    @Ignore
    public void shouldPublishReportDataIntoQueue() {
        LogData reportData = new LogData(LogType.REGISTRATION, "123");
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("1", reportData);
        publisher.publish(reportData);
    }
}
