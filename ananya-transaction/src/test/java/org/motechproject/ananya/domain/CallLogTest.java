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
        callLog.addItem(new CallLogItem(null, startTime, endTime));
        assertThat(callLog.startTime(), is(startTime));
    }

    @Test
    public void shouldReturnType() {
        CallLog callLogForCourse = new CallLog();
        callLogForCourse.addItem(new CallLogItem(CallFlowType.CALL, null, null));
        callLogForCourse.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, null, null));

        assertThat(callLogForCourse.getType(), is(CallFlowType.CERTIFICATECOURSE));

        CallLog callLogForJobAid = new CallLog();
        callLogForJobAid.addItem(new CallLogItem(CallFlowType.JOBAID, null, null));
        callLogForJobAid.addItem(new CallLogItem(CallFlowType.CALL, null, null));

        assertThat(callLogForJobAid.getType(), is(CallFlowType.JOBAID));

    }
}
