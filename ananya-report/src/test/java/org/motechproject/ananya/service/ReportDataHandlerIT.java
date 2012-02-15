package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.*;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;

    @Test
    public void shouldMapRegistrationTransactionDataToReportMeasure() {

        String locationCode = "S001D002B002V001";
        String msisdn = "555";

        Location location = new Location(locationCode, "district", "block", "panchayat");
        allLocations.add(location);

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, location.getId());
        frontLineWorker.setName("Name");
        frontLineWorker.setStatus(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.add(frontLineWorker);

        DateTime dateTime = DateTime.now();
        RegistrationLog registrationLog = new RegistrationLog(msisdn, "123", dateTime, dateTime.plusMinutes(1), "");
        allRegistrationLogs.add(registrationLog);

        LocationDimension locationDimension = new LocationDimension(locationCode, "district", "block", "panchayat");
        allLocationDimensions.add(locationDimension);

        LogData logData = new LogData(LogType.REGISTRATION, registrationLog.getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistration(event);

        TimeDimension timeDimension = allTimeDimensions.fetchFor(dateTime);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId(), timeDimension.getId(), locationDimension.getId());

        assertEquals(locationDimension.getLocationId(), locationCode);
        assertEquals(locationDimension.getDistrict(), "district");
        assertEquals(locationDimension.getBlock(), "block");
        assertEquals(locationDimension.getPanchayat(), "panchayat");

        assertEquals((int) timeDimension.getDay(), dateTime.getDayOfYear());
        assertEquals((int) timeDimension.getWeek(), dateTime.getWeekOfWeekyear());
        assertEquals((int) timeDimension.getMonth(), dateTime.getMonthOfYear());
        assertEquals((int) timeDimension.getYear(), dateTime.getYear());

        assertEquals(frontLineWorkerDimension.getMsisdn(), new Long(555));
        assertEquals(frontLineWorkerDimension.getOperator(), "");
        assertEquals(frontLineWorkerDimension.getName(), "Name");
        assertEquals(frontLineWorkerDimension.getStatus(), RegistrationStatus.REGISTERED.toString());

        assertNotNull(registrationMeasure);
        assertEquals("block", registrationMeasure.getLocationDimension().getBlock());
        assertEquals(locationCode, registrationMeasure.getLocationDimension().getLocationId());
        assertEquals(dateTime.getDayOfYear(), (int) registrationMeasure.getTimeDimension().getDay());
    }

    @Test
    public void shouldUpdateRegistrationStatusAndNameOnRegistrationCompletionEvent() {
        String msisdn = "555";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, "S001D002B002V001");
        frontLineWorker.setName("Name");
        frontLineWorker.setStatus(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.add(frontLineWorker);

        allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(msisdn), "", "", RegistrationStatus.PENDING_REGISTRATION.toString());

        LogData logData = new LogData(LogType.REGISTRATION_SAVE_NAME, frontLineWorker.getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistrationCompletion(event);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));

        assertEquals(frontLineWorkerDimension.getMsisdn(), new Long(555));
        assertEquals(frontLineWorkerDimension.getOperator(), "");
        assertEquals(frontLineWorkerDimension.getName(), "Name");
        assertEquals(frontLineWorkerDimension.getStatus(), RegistrationStatus.REGISTERED.toString());
    }
}
