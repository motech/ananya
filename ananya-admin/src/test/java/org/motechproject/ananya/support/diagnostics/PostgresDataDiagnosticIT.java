package org.motechproject.ananya.support.diagnostics;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCallDurationMeasures;
import org.motechproject.ananya.repository.measure.AllSMSSentMeasures;
import org.motechproject.ananya.support.TestDataAccessTemplate;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-admin.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class PostgresDataDiagnosticIT {

    @Autowired
    private PostgresDataDiagnostic postgresDiagnostic;
    @Autowired
    private TestDataAccessTemplate template;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllSMSSentMeasures allSMSSentMeasures;
    @Autowired
    private AllCallDurationMeasures allCallDurationMeasures;

    private Session session;

    @Before
    public void setUp() {
        resetDB();
        setupDB();
        session = template.getSessionFactory().openSession();
    }

    @After
    public void tearDown() {
        resetDB();
    }

    @Test
    public void shouldVerifyFLWTotalCountQueries() throws Exception {
        int totalFLWs = ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS.getQuery()).uniqueResult()).intValue();
        assertEquals(4, totalFLWs);

        List<Object[]> resultSet = session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS.getQuery()).list();
        for (Object[] resultRow : resultSet) {
            Object status = resultRow[1];
            Object count = resultRow[0];
            if (status.equals("REGISTERED")) assertEquals(1L, count);
            else if (status.equals("UNREGISTERED")) assertEquals(2L, count);
            else if (status.equals("PARTIALLY_REGISTERED")) assertEquals(1L, count);
        }
    }

    @Test
    public void shouldVerifyFLWTodayCountQueries() {
        DateTime today = DateTime.now();
        int todayFLWs = ((Long) session.createQuery(DiagnosticQuery.FIND_TODAY_FLWS.getQuery(today)).uniqueResult()).intValue();
        assertEquals(2, todayFLWs);

        List<Object[]> resultSet = session.createQuery(DiagnosticQuery.FIND_TODAY_FLWS_BY_STATUS.getQuery(today)).list();
        for (Object[] resultRow : resultSet) {
            Object status = resultRow[1];
            Object count = resultRow[0];
            if (status.equals("REGISTERED")) assertEquals(1L, count);
            else if (status.equals("UNREGISTERED")) assertEquals(1L, count);
            else if (status.equals("PARTIALLY_REGISTERED")) assertEquals(0L, count);
        }
    }

    @Test
    public void shouldVerifyAllSMSQueries() {
        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_SMS_SENT.getQuery()).uniqueResult()).intValue());
        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TODAY_SMS_SENT.getQuery(DateTime.now())).uniqueResult()).intValue());
    }

    @Test
    public void shouldVerifyTotalCallsQueries() {
        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_COURSE_CALLS.getQuery()).uniqueResult()).intValue());
        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS.getQuery()).uniqueResult()).intValue());
    }

    @Test
    public void shouldVerifyTodayCallsQueries() {
        DateTime today = DateTime.now();
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_TODAY_JOB_AID_CALLS.getQuery(today)).uniqueResult()).intValue());
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_TODAY_COURSE_CALLS.getQuery(today)).uniqueResult()).intValue());
    }

    private void setupDB() {
        String circle = "circle";
        String operator = "airtel";
        DateTime today = DateTime.now();
        DateTime yesterday = today.minusDays(1);
        long calledNumber = 57711L;

        Location defaultLocation = Location.getDefaultLocation();
        LocationDimension locationDimension = allLocationDimensions.saveOrUpdate(new LocationDimension(defaultLocation.getExternalId(), defaultLocation.getState(), defaultLocation.getDistrict(), defaultLocation.getBlock(), defaultLocation.getPanchayat(), "VALID"));

        TimeDimension todayTimeDimension = allTimeDimensions.addOrUpdate(today);
        TimeDimension yesterdayTimeDimension = allTimeDimensions.addOrUpdate(yesterday);

        FrontLineWorkerDimension flw1 = allFrontLineWorkerDimensions.createOrUpdate(99986574410L, null, operator, circle, "name", "AWW", "REGISTERED", UUID.randomUUID(), null);
        FrontLineWorkerDimension flw2 = allFrontLineWorkerDimensions.createOrUpdate(99986574411L, null, operator, circle, "", "", "UNREGISTERED", UUID.randomUUID(), null);
        FrontLineWorkerDimension flw3 = allFrontLineWorkerDimensions.createOrUpdate(99986574412L, null, operator, circle, "", "", "UNREGISTERED", UUID.randomUUID(), null);
        FrontLineWorkerDimension flw4 = allFrontLineWorkerDimensions.createOrUpdate(99986574413L, null, operator, circle, "", "", "PARTIALLY_REGISTERED", UUID.randomUUID(), null);

        CallDurationMeasure call1 = new CallDurationMeasure(flw1, locationDimension, todayTimeDimension, "99986574410-1111", calledNumber, 30, today, today.plusSeconds(30), "JOBAID", 1);
        CallDurationMeasure call1Peer = new CallDurationMeasure(flw1, locationDimension, todayTimeDimension, "99986574410-1111", calledNumber, 30, today, today.plusSeconds(30), "CALL", 1);
        CallDurationMeasure call2 = new CallDurationMeasure(flw2, locationDimension, todayTimeDimension, "99986574411-2222", calledNumber, 30, today, today.plusSeconds(30), "CERTIFICATECOURSE", 1);
        CallDurationMeasure call2Peer = new CallDurationMeasure(flw2, locationDimension, todayTimeDimension, "99986574411-2222", calledNumber, 30, today, today.plusSeconds(30), "CALL", 1);
        CallDurationMeasure call3 = new CallDurationMeasure(flw3, locationDimension, yesterdayTimeDimension, "99986574410-3333", calledNumber, 30, yesterday, yesterday.plusSeconds(30), "JOBAID", 1);
        CallDurationMeasure call3Peer = new CallDurationMeasure(flw3, locationDimension, yesterdayTimeDimension, "99986574410-3333", calledNumber, 30, yesterday, yesterday.plusSeconds(30), "CALL", 1);
        CallDurationMeasure call4 = new CallDurationMeasure(flw4, locationDimension, yesterdayTimeDimension, "99986574411-4444", calledNumber, 30, yesterday, yesterday.plusSeconds(30), "CERTIFICATECOURSE", 1);
        CallDurationMeasure call4Peer = new CallDurationMeasure(flw4, locationDimension, yesterdayTimeDimension, "99986574411-4444", calledNumber, 30, yesterday, yesterday.plusSeconds(30), "CALL", 1);

        allCallDurationMeasures.add(call1);
        allCallDurationMeasures.add(call1Peer);
        allCallDurationMeasures.add(call2);
        allCallDurationMeasures.add(call2Peer);
        allCallDurationMeasures.add(call3);
        allCallDurationMeasures.add(call3Peer);
        allCallDurationMeasures.add(call4);
        allCallDurationMeasures.add(call4Peer);

        allSMSSentMeasures.save(new SMSSentMeasure(1, "", true, flw1, todayTimeDimension, locationDimension));
        allSMSSentMeasures.save(new SMSSentMeasure(1, "", true, flw2, todayTimeDimension, locationDimension));
        allSMSSentMeasures.save(new SMSSentMeasure(1, "", false, flw3, yesterdayTimeDimension, locationDimension));

    }

    private void resetDB() {
        List<Class<? extends Object>> entities = Arrays.asList(
                FrontLineWorkerDimension.class, TimeDimension.class, LocationDimension.class,
                CourseItemDimension.class, JobAidContentDimension.class, JobAidContentMeasure.class,
                CourseItemMeasure.class, RegistrationMeasure.class, SMSSentMeasure.class);

        for (Class<? extends Object> entity : entities)
            template.deleteAll(template.loadAll(entity));

    }


}
