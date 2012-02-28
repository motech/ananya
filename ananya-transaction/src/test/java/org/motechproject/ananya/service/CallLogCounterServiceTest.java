package org.motechproject.ananya.service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallLogCounter;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-transaction.xml")
public class CallLogCounterServiceTest {

    @Mock
    private AllCallLogCounters allCallLogCounters;
    
    private CallLogCounterService callLogCounterService;
    
    @Before
    public void setUp() {
        initMocks(this);
        callLogCounterService = new CallLogCounterService(allCallLogCounters);
    }
    
    
    @Test
    public void shouldPurgeRedundantPacketsFromCallLogCountersWhenCallLogCounterExists() {

        String callId = "555:123";
        
        ArrayList<TransferData> dataCollection = new ArrayList<TransferData>();
        dataCollection.add(new TransferData("0", TransferData.TYPE_CC_STATE));
        dataCollection.add(new TransferData("1", TransferData.TYPE_CALL_DURATION));
        dataCollection.add(new TransferData("2", TransferData.TYPE_CC_STATE));
        dataCollection.add(new TransferData("3", TransferData.TYPE_CC_STATE));
        dataCollection.add(new TransferData("4", TransferData.TYPE_CC_STATE));
        dataCollection.add(new TransferData("5", TransferData.TYPE_CALL_DURATION));

        when(allCallLogCounters.findByCallId(callId)).thenReturn(new CallLogCounter(callId, 3));

        callLogCounterService.purgeRedundantPackets(callId, dataCollection);

        verify(allCallLogCounters).update(argThat(new CallCounterMatcher(new CallLogCounter(callId, 5))));

        assertEquals(dataCollection.size(), 2);
        assertEquals(dataCollection.get(0).getToken(), "4");
        assertEquals(dataCollection.get(1).getToken(), "5");
    }

    public static class CallCounterMatcher extends BaseMatcher<CallLogCounter> {

        private CallLogCounter callLogCounter;
        
        public CallCounterMatcher(CallLogCounter callLogCounter) {
            this.callLogCounter = callLogCounter;
        }
        
        @Override
        public boolean matches(Object o) {
            CallLogCounter matchCounter = (CallLogCounter) o;
            return matchCounter.getCallId().equals(callLogCounter.getCallId()) &&
                   matchCounter.getToken().equals(callLogCounter.getToken());
        }

        @Override
        public void describeTo(Description description) {
        }
    }
    
}
