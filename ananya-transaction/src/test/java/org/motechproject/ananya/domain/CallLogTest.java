package org.motechproject.ananya.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CallLogTest {

    @Test
    public void shouldReturnTrueIfItHasNoItems() {
        CallLog callLog = new CallLog();
        assertTrue(callLog.hasNoItems());

        callLog.addItem(new CallLogItem());
        assertFalse(callLog.hasNoItems());
    }

    @Test
    public void shouldReturnStartTime() {
        CallLog callLog = new CallLog();
        assertNull(callLog.startTime());

        DateTime startTime = DateUtil.now();
        DateTime endTime = startTime.plusMinutes(2);
        callLog.addItem(new CallLogItem(null,startTime, endTime));
        assertThat(callLog.startTime(),is(startTime));
    }
}
