package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CallDurationListTest {

    private CallDurationList callDurationList;

    @Before
    public void setUp() {
        callDurationList = new CallDurationList();
    }

    @Test
    public void shouldConvertJsonAndAddToList() {

        callDurationList.add("123456", "123", "{\"callEvent\":\"CALL_START\",\"time\":1331211295810}");
        callDurationList.add("123456", "123", "{\"callEvent\":\"CERTIFICATECOURSE_START\",\"time\":1331211297476}");

        List<CallDuration> callDurations = callDurationList.all();

        assertThat(callDurations.size(), is(2));

        CallDuration callDuration1 = callDurations.get(0);
        assertThat(callDuration1.getCallEvent(), is(CallEvent.CALL_START));
        CallDuration callDuration2 = callDurations.get(1);
        assertThat(callDuration2.getCallEvent(), is(CallEvent.CERTIFICATECOURSE_START));

    }
}
