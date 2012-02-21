package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-transaction.xml")
public class AllCallDetailLogsTest {
    @Autowired
    private AllCallDetailLogs allCallDetailLogs;

    @After
    public void tearDown() {
        allCallDetailLogs.removeAll();
    }

    @Test
    public void shouldSaveACallDetailLog() {
        CallDetailLog log = new CallDetailLog("caller", "callerId", CallEvent.REGISTRATION_START, DateTime.now(),"");
        assertThat(log.getId(), is(nullValue()));
        allCallDetailLogs.add(log);
        assertThat(log.getId(), is(notNullValue()));
        CallDetailLog logFromDb = allCallDetailLogs.get(log.getId());
        assertThat(logFromDb.getCallId(), is(log.getCallId()));
    }

}
