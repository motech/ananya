package org.motechproject.ananya.repository;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-transaction.xml")
public class AllCertificateCourseLogsTest {
    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;

    @Before
    @After
    public void tearDown() {
        allCertificateCourseLogs.removeAll();
    }

    @Test
    public void shouldSaveACertificationLog() {
        CertificationCourseLog log = new CertificationCourseLog();//"caller", "9999990000", "1", null, null, null, null, null);
        assertThat(log.getId(), is(nullValue()));
        allCertificateCourseLogs.add(log);
        assertThat(log.getId(), is(notNullValue()));
        final CertificationCourseLog logFromDb = allCertificateCourseLogs.get(log.getId());
        assertThat(logFromDb.getCallId(), is(log.getCallId()));
    }

    @Test
    public void shouldDeleteAllCCLogsForInvalidMsisdn() {
        String invalidCallerId1 = "123E+12";
        String invalidCallerId2 = "123E12";
        String validCallerId = "12312";
        CertificationCourseLog log1 = new CertificationCourseLog(invalidCallerId1, null, null, null, null);
        CertificationCourseLog log2 = new CertificationCourseLog(invalidCallerId2, null, null, null, null);
        CertificationCourseLog log3 = new CertificationCourseLog(validCallerId, null, null, null, null);
        allCertificateCourseLogs.add(log1);
        allCertificateCourseLogs.add(log2);
        allCertificateCourseLogs.add(log3);

        allCertificateCourseLogs.deleteCCLogsForInvalidMsisdns();

        List<CertificationCourseLog> actualCCLogs = allCertificateCourseLogs.getAll();
        assertEquals(1, actualCCLogs.size());
        assertEquals(validCallerId, actualCCLogs.get(0).getCallerId());
    }

    @Test
    public void shouldDeleteCertificateCourseLogForAGivenCallId() {
        String callId = "callId";
        CertificationCourseLog entity = new CertificationCourseLog("callerId", "number", "", callId, "");
        allCertificateCourseLogs.add(entity);
        assertNotNull(allCertificateCourseLogs.findByCallId(callId));

        allCertificateCourseLogs.deleteFor(callId);
        assertNull(allCertificateCourseLogs.findByCallId(callId));
    }

    @Test
    public void shouldGracefullyHandleDeleteCertificateCourseLogForAGivenCallIdWhenDoesNotExist() {
        String callId = "callId";
        assertNull(allCertificateCourseLogs.findByCallId(callId));

        allCertificateCourseLogs.deleteFor(callId);
        assertNull(allCertificateCourseLogs.findByCallId(callId));
    }

    public static class CertificationCourseLogMatcher extends BaseMatcher<CertificationCourseLog> {
        private CertificationCourseLog log;

        public CertificationCourseLogMatcher(CertificationCourseLog log) {
            this.log = log;
        }

        @Override
        public void describeTo(Description description) {

        }

        @Override
        public boolean matches(Object o) {
            CertificationCourseLog actualLog = (CertificationCourseLog) o;
            return log.getCallId().equals(actualLog.getCallId());
        }
    }
}