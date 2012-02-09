package org.motechproject.ananya.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.log.LogData;
import org.motechproject.ananya.domain.log.LogType;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class ReportDataHandlerIT {

    @Autowired
    private ReportDataHandler handler;
    @Autowired
    private AllRegistrationLogs allRegistrationLogs;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;
    @Autowired
    private ReportDB reportDB;

    @Test
    @Ignore
    public void shouldMapRegistrationTransactionDataToReportMeasure() {

        Location location = new Location("S001D002B002V001", "district", "block", "panchayat");
        allLocations.add(location);

//        allFrontLineWorkers.add(frontLineWorker);


        LogData logData = new LogData(LogType.REGISTRATION, "1234");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistration(event);
    }

}
