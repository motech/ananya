package org.motechproject.ananya.support.diagnostics;

import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.*;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.dimension.*;
import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.motechproject.ananya.repository.measure.AllJobAidContentMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.repository.measure.AllSMSSentMeasures;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticLog;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class PostgresDiagnosticIT {

    @Autowired
    private PostgresDiagnostic postgresDiagnostic;
    @Qualifier("testDataAccessTemplate")
    @Autowired
    private TestDataAccessTemplate template;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;
    @Autowired
    private AllJobAidContentMeasures allJobAidContentMeasures;
    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;
    @Autowired
    private AllCourseItemMeasures allCourseItemMeasures;
    @Autowired
    private AllSMSSentMeasures allSMSSentMeasures;

    private Session session;
    private String callId;
    private DateTime today;
    private DateTime anotherDay;

    @Before
    public void setUp() {
        resetDB();
        session = template.getSessionFactory().openSession();
        callId = "123";
        today = DateTime.now();
        anotherDay = new DateTime(1234l);
    }

    @After
    public void tearDown() {
        resetDB();
    }

    private void resetDB() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
        template.deleteAll(template.loadAll(JobAidContentMeasure.class));
        template.deleteAll(template.loadAll(CourseItemMeasure.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
    }

    @Test
    public void shouldTestFLWCount() throws Exception {
        String circle = "circle";
        LocationDimension locationDimension = allLocationDimensions.add(new LocationDimension("id", "", "", ""));
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(today);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(987L, "", circle, "", "", "REGISTERED");
        allFrontLineWorkerDimensions.getOrMakeFor(654L, "", circle, "", "", "UNREGISTERED");
        allFrontLineWorkerDimensions.getOrMakeFor(653L, "", circle, "", "", "UNREGISTERED");
        allFrontLineWorkerDimensions.getOrMakeFor(321L, "", circle, "", "", "PARTIALLY_REGISTERED");
        allRegistrationMeasures.add(new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension));

        assertEquals(4, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS.getQuery()).uniqueResult()).intValue());
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_FLWS_REG_TODAY.getQuery(today)).uniqueResult()).intValue());

        List<Object[]> resultSet = session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS.getQuery()).list();
        for (Object[] resultRow : resultSet) {
            if (resultRow[1].equals("REGISTERED"))
                assertEquals(Long.valueOf(1), resultRow[0]);
            else if (resultRow[1].equals("UNREGISTERED"))
                assertEquals(Long.valueOf(2), resultRow[0]);
            else if (resultRow[1].equals("PARTIALLY_REGISTERED"))
                assertEquals(Long.valueOf(1), resultRow[0]);
        }

        List<Object[]> resultSetForToday = session.createQuery(DiagnosticQuery.FIND_TOTAL_FLWS_BY_STATUS.getQuery()).list();
        for (Object[] resultRow : resultSetForToday) {
            if (resultRow[1].equals("REGISTERED"))
                assertEquals(Long.valueOf(1), resultRow[0]);
        }
    }

    @Test
    public void shouldTestJobAidCallCount() throws Exception {

        LocationDimension locationDimension = allLocationDimensions.add(new LocationDimension("id", "", "", ""));
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(today);
        TimeDimension anotherTimeDimension = allTimeDimensions.addOrUpdate(anotherDay);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(987L, "", "circle", "", "", "REGISTERED");
        allJobAidContentDimensions.add(new JobAidContentDimension("contentId", null, "", "", "", 0));
        JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId("contentId");
        allJobAidContentMeasures.add(new JobAidContentMeasure(frontLineWorkerDimension, "123", locationDimension, jobAidContentDimension, timeDimension, today, 0, 0));
        allJobAidContentMeasures.add(new JobAidContentMeasure(frontLineWorkerDimension, "456", locationDimension, jobAidContentDimension, anotherTimeDimension, today, 0, 0));
        allJobAidContentMeasures.add(new JobAidContentMeasure(frontLineWorkerDimension, "123", locationDimension, jobAidContentDimension, timeDimension, today, 0, 0));

        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_JOB_AID_CALLS.getQuery()).uniqueResult()).intValue());
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_JOB_AID_CALLS_TODAY.getQuery(today)).uniqueResult()).intValue());
    }

    @Test
    public void shouldTestCertificateCourseCallCount() throws Exception {
        LocationDimension locationDimension = allLocationDimensions.add(new LocationDimension("id", "", "", ""));
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(today);
        TimeDimension anotherTimeDimension = allTimeDimensions.addOrUpdate(anotherDay);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(987L, "", "circle", "", "", "REGISTERED");
        allCourseItemDimensions.add(new CourseItemDimension("", "contenId", CourseItemType.COURSE, null));
        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor("contenId");
        allCourseItemMeasures.save(new CourseItemMeasure(timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, 0, CourseItemState.START, "123"));
        allCourseItemMeasures.save(new CourseItemMeasure(timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, 0, CourseItemState.START, "123"));
        allCourseItemMeasures.save(new CourseItemMeasure(anotherTimeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, 0, CourseItemState.START, "456"));

        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_CCOURSE_CALLS.getQuery()).uniqueResult()).intValue());
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_CCOURSE_CALLS_TODAY.getQuery(today)).uniqueResult()).intValue());
    }

    @Test
    public void shouldTestSMSSentCount() throws Exception {
        LocationDimension locationDimension = allLocationDimensions.add(new LocationDimension("id", "", "", ""));
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(today);
        TimeDimension anotherTimeDimension = allTimeDimensions.addOrUpdate(anotherDay);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(987L, "", "circle", "", "", "REGISTERED");
        allSMSSentMeasures.save(new SMSSentMeasure(1,"",true,frontLineWorkerDimension, timeDimension, locationDimension));
        allSMSSentMeasures.save(new SMSSentMeasure(1,"",true,frontLineWorkerDimension, anotherTimeDimension, locationDimension));
        allSMSSentMeasures.save(new SMSSentMeasure(1,"",false,frontLineWorkerDimension, timeDimension, locationDimension));

        assertEquals(2, ((Long) session.createQuery(DiagnosticQuery.FIND_TOTAL_SMS_SENT.getQuery()).uniqueResult()).intValue());
        assertEquals(1, ((Long) session.createQuery(DiagnosticQuery.FIND_SMS_SENT_TODAY.getQuery(today)).uniqueResult()).intValue());
    }

    @Test
    public void shouldDiagnosePostgres() {
        DiagnosticLog diagnosticLog = postgresDiagnostic.performDiagnosis();
        System.out.println(diagnosticLog.toString());
        assertNotNull(diagnosticLog);
    }

}
