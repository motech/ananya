package org.motechproject.ananya.repository;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.log.CertificationCourseLog;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AllCertificationCourseLogsTest extends SpringIntegrationTest {
    @Autowired
    private AllCertificationCourseLogs allCertificationCourseLogs;

    @After
    public void tearDown() {
        allCertificationCourseLogs.removeAll();
    }

    @Test
    public void shouldSaveACertificationLog() {
        CertificationCourseLog log = new CertificationCourseLog("caller", "9999990000", "1");
        assertThat(log.getId(), is(nullValue()));
        allCertificationCourseLogs.add(log);
        assertThat(log.getId(), is(notNullValue()));
        final CertificationCourseLog logFromDb = allCertificationCourseLogs.get(log.getId());
        assertThat(logFromDb.getCallId(), is(log.getCallId()));
    }

    @Test
    public void shouldGetALogByCallIdAndToken() {
        final String callId = "9999990000";
        CertificationCourseLog log1 = new CertificationCourseLog("caller", callId, "1");
        CertificationCourseLog log2 = new CertificationCourseLog("caller", callId, "2");
        CertificationCourseLog log3 = new CertificationCourseLog("caller", callId, "3");
        allCertificationCourseLogs.add(log1);
        allCertificationCourseLogs.add(log2);
        allCertificationCourseLogs.add(log3);

        final CertificationCourseLog log3FromDb = allCertificationCourseLogs.findByCallIdAndToken(callId, "3");
        assertThat(log3FromDb, is(new CertificationCourseLogMatcher(log3)));
    }

    @Test
    public void shouldAddALogOnlyIfAbsent() {
        final String callId = "9999990000";
        CertificationCourseLog log1 = new CertificationCourseLog("caller", callId, "1");
        assertThat(allCertificationCourseLogs.addIfAbsent(log1), is(true));
        assertThat(allCertificationCourseLogs.addIfAbsent(log1), is(false));
    }

    public static class CertificationCourseLogMatcher extends BaseMatcher<CertificationCourseLog> {
        private CertificationCourseLog log;

        public CertificationCourseLogMatcher(CertificationCourseLog log) {
            this.log = log;
        }

        @Override
        public void describeTo(Description description) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean matches(Object o) {
            CertificationCourseLog actualLog = (CertificationCourseLog)o;
            return log.getCallId().equals(actualLog.getCallId()) && log.getToken().equals(actualLog.getToken());
        }
    }
}