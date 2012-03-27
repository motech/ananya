package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CallLogCounter;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CallLogCounterService {

    private AllCallLogCounters allCallLogCounters;
    private static Object lockObject = new Object();
    private static Logger log = LoggerFactory.getLogger(CallLogCounterService.class);


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
    public void purgeRedundantPackets(final String callId, final Collection<TransferData> dataCollection) {
        String callIdLockObject;

        int maxTokenValue = Collections.max(dataCollection, new Comparator<TransferData>() {
            @Override
            public int compare(TransferData transferData1, TransferData transferData2) {
                return transferData1.tokenIntValue() - transferData2.tokenIntValue();
            }
        }).tokenIntValue();

        List<TransferData> packetsToPurge = new ArrayList<TransferData>();
        synchronized (lockObject) {
            callIdLockObject = callId.intern();
        }
        synchronized (callIdLockObject) {
            CallLogCounter currentCallCounter = allCallLogCounters.findByCallId(callId);

            if(currentCallCounter == null) {
                currentCallCounter = new CallLogCounter(callId, maxTokenValue);
                allCallLogCounters.add(currentCallCounter);
                return;
            }

            for(TransferData transferData : dataCollection) {
                if(currentCallCounter.getToken() >= transferData.tokenIntValue()) {
                    packetsToPurge.add(transferData);
                    log.info("Purged Redundant Packet" + transferData.tokenIntValue());
                }
            }

            dataCollection.removeAll(packetsToPurge);

            currentCallCounter.setToken(maxTokenValue);
            allCallLogCounters.update(currentCallCounter);
        }

    }
}
