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

    @Autowired
    public CallLogCounterService(AllCallLogCounters allCallLogCounters) {
        this.allCallLogCounters = allCallLogCounters;
    }

    public void purgeRedundantTokens(final String callId, final TransferDataList transferDataList) {
        Integer maxToken = transferDataList.maxToken();
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
