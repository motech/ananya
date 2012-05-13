package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class AudioTrackerSynchronizerIT {

    @Autowired
    private AudioTrackerSynchroniser audioTrackerSynchronizer;

    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Before
    public void setUp() {
        resetDB();
    }

    @After
    public void tearDown() {
        resetDB();
    }

    @Test
    public void shouldReturnLowPriority() {
        Priority priority = audioTrackerSynchronizer.runPriority();

        assertEquals(Priority.low, priority);
    }

    @Test
    public void shouldMigrateAudioTrackerLogsFromCouchDbToPostgresForCertificateCourse() {
        String callerId = "1234";
        String callId = "12345678";
        String contentId = "contentId";
        String timeStamp = "1336045431373";
        setUpTransactionData(callerId, contentId, timeStamp);
        setUpReportDataForCertificateCourse(callId, callerId, contentId, timeStamp);

        audioTrackerSynchronizer.replicate(DateTime.now(), DateTime.now().plusHours(8));

        verifyPostgresDataForCertificateCourse(callerId, contentId);
    }

    @Test
    public void shouldMigrateAudioTrackerLogsFromCouchDbToPostgresForJobAid() {
        String callerId = "1234";
        String callId = "12345678";
        String contentId = "contentId";
        String timeStamp = "1336045431373";
        setUpTransactionData(callerId, contentId, timeStamp);
        setUpReportDataForJobAid(callId, callerId, contentId, timeStamp);

        audioTrackerSynchronizer.replicate(DateTime.now(), DateTime.now().plusHours(8));

        verifyPostgresDataForJobAid(callerId, contentId);
    }

    private void resetDB() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
        template.deleteAll(template.loadAll(JobAidContentMeasure.class));
        allAudioTrackerLogs.removeAll();
    }

    private void setUpTransactionData(String callerId, String contentId, String timeStamp) {
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), "airtel", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString());
        template.save(frontLineWorkerDimension);
        LocationDimension locationDimension = new LocationDimension("locationId", "district", "block", "panchayat");
        template.save(locationDimension);
        TimeDimension timeDimension = new TimeDimension(new DateTime(new Long(timeStamp)));
        template.save(timeDimension);
        template.save(new CourseItemDimension("name", contentId, CourseItemType.AUDIO, null, "filename", 123));
        template.save(new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension));
        template.save(new JobAidContentDimension("contentId", null, "name", "filename", "type", 123));
    }

    private void setUpReportDataForCertificateCourse(String callId, String callerId, String contentId, String timeStamp) {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);
        audioTrackerLog.addItem(new AudioTrackerLogItem(contentId, new DateTime(new Long(timeStamp)), 123));
        allAudioTrackerLogs.add(audioTrackerLog);
    }

    private void setUpReportDataForJobAid(String callId, String callerId, String contentId, String timeStamp) {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        audioTrackerLog.addItem(new AudioTrackerLogItem(contentId, new DateTime(new Long(timeStamp)), 123));
        allAudioTrackerLogs.add(audioTrackerLog);
    }

    private void verifyPostgresDataForCertificateCourse(String callerId, String contentId) {
        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);
        CourseItemMeasure courseItemMeasureFromDb = null;
        for (CourseItemMeasure courseItemMeasure : courseItemMeasures)
            if (courseItemMeasure.getCourseItemDimension().getContentId().equals(contentId))
                courseItemMeasureFromDb = courseItemMeasure;
        assertNotNull(courseItemMeasureFromDb);
        assertThat(courseItemMeasureFromDb.getFrontLineWorkerDimension().getMsisdn(), is(new Long(callerId)));
        assertThat(courseItemMeasureFromDb.getCourseItemDimension().getType(), is(CourseItemType.AUDIO));
        List<JobAidContentMeasure> jobAidContentMeasures = template.loadAll(JobAidContentMeasure.class);
        assertEquals(0, jobAidContentMeasures.size());
    }

    private void verifyPostgresDataForJobAid(String callerId, String contentId) {
        List<JobAidContentMeasure> jobAidContentMeasures = template.loadAll(JobAidContentMeasure.class);
        JobAidContentMeasure jobAidContentMeasureFromDb = null;
        for (JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasures)
            if (jobAidContentMeasure.getJobAidContentDimension().getContentId().equals(contentId))
                jobAidContentMeasureFromDb = jobAidContentMeasure;
        assertNotNull(jobAidContentMeasureFromDb);
        assertThat(jobAidContentMeasureFromDb.getFrontLineWorkerDimension().getMsisdn(), is(new Long(callerId)));
        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);
        assertEquals(0, courseItemMeasures.size());
    }

}
