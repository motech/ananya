package org.motechproject.ananya.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AudioTrackerLogTest {

    @Test
    public void shouldReturnTrueIfItHasNoAudioTrackerLogItems() {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog();
        assertTrue(audioTrackerLog.hasNoItems());

        audioTrackerLog.addItem(new AudioTrackerLogItem());
        assertFalse(audioTrackerLog.hasNoItems());
    }

    @Test
    public void shouldReturnTimeOfLogFromItems() {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog();
        assertNull(audioTrackerLog.time());

        DateTime now = DateUtil.now();
        audioTrackerLog.addItem(new AudioTrackerLogItem("cid", "language", now, 123));
        assertThat(audioTrackerLog.time(), is(now));
    }
}
