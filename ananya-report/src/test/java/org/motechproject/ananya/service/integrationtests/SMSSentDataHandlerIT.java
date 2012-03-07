package org.motechproject.ananya.service.integrationtests;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.handler.SMSSentDataHandler;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class SMSSentDataHandlerIT extends SpringIntegrationTest {
    @Autowired
    SMSSentDataHandler handler;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;

    @After
    public void tearDown(){
        allFrontLineWorkers.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
    }

    @Test
    public void shouldLogSMSSentMeasure() {
        String msisdn = "9" + System.currentTimeMillis();
        FrontLineWorker flw = new FrontLineWorker(msisdn, Designation.ANGANWADI, "S001D001B012V001", null).status(RegistrationStatus.REGISTERED);
        flw.incrementCertificateCourseAttempts();
        flw.addSMSReferenceNumber("001012" + msisdn + "01");
        allFrontLineWorkers.add(flw);

        DateTime now = DateTime.now();
        TimeDimension timeDimension = allTimeDimensions.makeFor(now);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(msisdn), "airtel", "Rani", "REGISTERED");

        LogData logData = new LogData(LogType.SMS_SENT, msisdn);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleSMSSent(event);

        List<SMSSentMeasure> smsSentMeasures = template.loadAll(SMSSentMeasure.class);

        assertEquals(1, smsSentMeasures.size());
    }
}
