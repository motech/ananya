package org.motechproject.ananya.support.synchroniser;


import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLogItem;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class CertificateCourseItemSynchroniserIT {

    @Autowired
    private CertificateCourseItemSynchroniser certificateCourseItemSychroniser;
    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;
    @Qualifier("testDataAccessTemplate")
    @Autowired
    private TestDataAccessTemplate template;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @Before
    public void setUp() {
        resetDB();
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
        template.deleteAll(template.loadAll(CourseItemMeasure.class));
        allCertificateCourseLogs.removeAll();
    }

    @Test
    public void shouldMigrateCallDurationDataFromTransactionDBToReportDB() {
        String callerId = "1234";
        String callId = "1234-5678";
        DateTime callStartTime = DateUtil.now();
        String contentId = "contentId";
        String contentName = "Chapter4";
        CourseItemType courseItemType = CourseItemType.CHAPTER;

        LocationDimension locationDimension = new LocationDimension("locationId", "district", "block", "panchayat");
        template.save(locationDimension);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf(callerId), "airtel", "circle", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString());
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(callStartTime);
        template.save(new RegistrationMeasure(frontLineWorkerDimension,locationDimension,timeDimension));
        allCourseItemDimensions.add(new CourseItemDimension(contentName, contentId, courseItemType, null));

        CertificationCourseLog certificationCourseLog = new CertificationCourseLog(callerId, "9909", "airtel", callId, "1");
        CertificationCourseLogItem courseLogItem = new CertificationCourseLogItem(contentId, courseItemType, contentName, "3", CourseItemState.START, DateUtil.now());
        certificationCourseLog.addCourseLogItem(courseLogItem);
        allCertificateCourseLogs.add(certificationCourseLog);

        DateTime fromDate = DateUtil.now();
        DateTime toDate = fromDate.plusHours(8);

        SynchroniserLog synchroniserLog = certificateCourseItemSychroniser.replicate(fromDate, toDate);

        verifyCourseItemMeasureInReportDb(contentId, frontLineWorkerDimension);
        verifySynchroniserLog(synchroniserLog);
        assertTrue(allCertificateCourseLogs.getAll().isEmpty());
    }

    private void verifyCourseItemMeasureInReportDb(String contentId, FrontLineWorkerDimension frontLineWorkerDimension) {
        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);
        CourseItemMeasure courseItemMeasureFromDb = null;
        for (CourseItemMeasure courseItemMeasure : courseItemMeasures)
            if (courseItemMeasure.getCourseItemDimension().getContentId().equals(contentId))
                courseItemMeasureFromDb = courseItemMeasure;
        assertNotNull(courseItemMeasureFromDb);
        assertThat(courseItemMeasureFromDb.getFrontLineWorkerDimension().getMsisdn(), is(frontLineWorkerDimension.getMsisdn()));
    }

    private void verifySynchroniserLog(SynchroniserLog synchroniserLog) {
        List<SynchroniserLogItem> synchroniserLogItems = synchroniserLog.getItems();
        assertThat(synchroniserLogItems.size(), is(1));
        assertThat(synchroniserLogItems.get(0).print(), is("1234-5678: Success"));
    }

}
