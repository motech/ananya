package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CallLogCounter;
import org.motechproject.ananya.domain.TransferDataList;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallLogCounterService {

    private static Logger log = LoggerFactory.getLogger(CallLogCounterService.class);
    private AllCallLogCounters allCallLogCounters;
    private static Object lockObject = new Object();


    @Autowired
    public CallLogCounterService(AllCallLogCounters allCallLogCounters) {
        this.allCallLogCounters = allCallLogCounters;
    }

    /*
     * Supposed to remove log packets from the collection, that may have already been added to the DB. This faces a
     * typical concurrency issue, where multiple threads might try to modify the same record. It is counter by using
     * thread locks.
     * Since using a lock on a static object throughout the class would block a lot of threads, the code first
     * uses the static object to obtain another object that can be used for locking, but is unique to one particular
     * call. Since there is a very low possibility of the same call being accessed through multiple threads, it
     * doesn't create a blocking issue and prevents duplication of data.
     */
    public void purgeRedundantTokens(final String callId, final TransferDataList transferDataList) {

        String callIdLockObject;
        Integer maxToken = transferDataList.maxToken();

        synchronized (lockObject) {
            callIdLockObject = callId.intern();
        }
        synchronized (callIdLockObject) {
            CallLogCounter currentCallLogCounter = allCallLogCounters.findByCallId(callId);
            if (currentCallLogCounter == null) {
                currentCallLogCounter = new CallLogCounter(callId, maxToken);
                allCallLogCounters.add(currentCallLogCounter);
                return;
            }
            transferDataList.removeTokensOlderThan(currentCallLogCounter.getToken());
            currentCallLogCounter.setToken(maxToken);
            allCallLogCounters.update(currentCallLogCounter);
            log.info("updated token counter for " + callId);
        }

    }
}
