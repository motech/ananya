package org.motechproject.ananya.repository;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-transaction.xml")
public class AllCertificationCourseLogsTest {
    @Autowired
    private AllCertificationCourseLogs allCertificationCourseLogs;

    @After
    public void tearDown() {
        allCertificationCourseLogs.removeAll();
    }

    @Test
    public void shouldSaveACertificationLog() {
        CertificationCourseLog log = new CertificationCourseLog();//"caller", "9999990000", "1", null, null, null, null, null);
        assertThat(log.getId(), is(nullValue()));
        allCertificationCourseLogs.add(log);
        assertThat(log.getId(), is(notNullValue()));
        final CertificationCourseLog logFromDb = allCertificationCourseLogs.get(log.getId());
        assertThat(logFromDb.getCallId(), is(log.getCallId()));
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
            CertificationCourseLog actualLog = (CertificationCourseLog)o;
            return log.getCallId().equals(actualLog.getCallId());
        }
    }
}