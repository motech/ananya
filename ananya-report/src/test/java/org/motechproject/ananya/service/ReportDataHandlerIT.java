package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.*;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
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
    @Autowired
    private DataAccessTemplate template;

    @Before
    public void setUp(){
        cleanDB();
    }

    @After
    public void tearDown(){
        cleanDB();
    }

    private void cleanDB() {
        allFrontLineWorkers.removeAll();
        allRegistrationLogs.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
//        template.deleteAll(template.loadAll(RegistrationMeasure.class));
    }

    @Test
    public void shouldMapRegistrationTransactionDataToReportMeasure() {

        String locationCode = "S001D002B002V001";
        String msisdn = "555";

        Location location = new Location(locationCode, "district", "block", "panchayat");
        allLocations.add(location);

        DateTime registeredDate = DateTime.now();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, location.getId(),"");
        frontLineWorker.name("Name");
        frontLineWorker.status(RegistrationStatus.REGISTERED);
        frontLineWorker.setRegisteredDate(registeredDate);

        allFrontLineWorkers.add(frontLineWorker);

        DateTime dateTime = DateTime.now().plusDays(3);
        RegistrationLog registrationLog = new RegistrationLog(msisdn, "123", dateTime, dateTime.plusMinutes(1), "");
        allRegistrationLogs.add(registrationLog);

        LocationDimension locationDimension = new LocationDimension(locationCode, "district", "block", "panchayat");
        allLocationDimensions.add(locationDimension);
        allTimeDimensions.makeFor(registeredDate);

        LogData logData = new LogData(LogType.REGISTRATION, registrationLog.getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistration(event);

        TimeDimension timeDimension = allTimeDimensions.getFor(registeredDate);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId(), timeDimension.getId(), locationDimension.getId());

        assertEquals(locationDimension.getLocationId(), locationCode);
        assertEquals(locationDimension.getDistrict(), "district");
        assertEquals(locationDimension.getBlock(), "block");
        assertEquals(locationDimension.getPanchayat(), "panchayat");

        assertEquals((int) timeDimension.getDay(), registeredDate.getDayOfYear());
        assertEquals((int) timeDimension.getWeek(), registeredDate.getWeekOfWeekyear());
        assertEquals((int) timeDimension.getMonth(), registeredDate.getMonthOfYear());
        assertEquals((int) timeDimension.getYear(), registeredDate.getYear());

        assertEquals(frontLineWorkerDimension.getMsisdn(), new Long(555));
        assertEquals(frontLineWorkerDimension.getOperator(), "");
        assertEquals(frontLineWorkerDimension.getName(), "Name");
        assertEquals(frontLineWorkerDimension.getStatus(), RegistrationStatus.REGISTERED.toString());

        assertNotNull(registrationMeasure);
        assertEquals("block", registrationMeasure.getLocationDimension().getBlock());
        assertEquals(locationCode, registrationMeasure.getLocationDimension().getLocationId());
        assertEquals(registeredDate.getDayOfYear(), (int) registrationMeasure.getTimeDimension().getDay());
    }

    @Test
    public void shouldUpdateRegistrationStatusAndNameOnRegistrationCompletionEvent() {
        String msisdn = "555";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, "S001D002B002V001","");
        frontLineWorker.name("Name");
        frontLineWorker.status(RegistrationStatus.REGISTERED);
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
