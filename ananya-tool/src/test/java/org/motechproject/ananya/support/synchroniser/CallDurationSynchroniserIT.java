package org.motechproject.ananya.support.synchroniser;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.motechproject.ananya.support.log.SynchroniserLogItem;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class CallDurationSynchroniserIT {

    @Autowired
    private CallDurationSychroniser callDurationSychroniser;
    @Autowired
    private AllCallLogs allCallLogs;
    @Autowired
    private DataAccessTemplate template;

    @Before
    public void setUp() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
        allCallLogs.removeAll();
    }

    @Test
    public void shouldMigrateCallDurationDataFromTransactionDBToReportDB() {
        String callerId = "1234";
        String callId = "1234-5678";

        setUpTransactionData(callerId);
        setUpReportData(callerId, callId);

        DateTime fromDate = DateUtil.now();
        DateTime toDate = fromDate.plusHours(8);

        SynchroniserLog synchroniserLog = callDurationSychroniser.replicate(fromDate, toDate);

        verifyCallDurationMeasureInReportDB(callId);
        verifySynchroniserLog(synchroniserLog);
    }

    private void setUpReportData(String callerId, String callId) {
        CallLog callLog = new CallLog(callId, callerId);
        callLog.addItem(new CallLogItem(CallFlowType.CALL, DateUtil.now(), DateUtil.now().plusSeconds(20)));
        allCallLogs.add(callLog);
    }

    private void setUpTransactionData(String callerId) {
        template.save(new FrontLineWorkerDimension(Long.valueOf(callerId), "airtel", "name", RegistrationStatus.PARTIALLY_REGISTERED.toString()));
    }

    private void verifySynchroniserLog(SynchroniserLog synchroniserLog) {
        List<SynchroniserLogItem> synchroniserLogItems = synchroniserLog.getItems();
        assertThat(synchroniserLogItems.size(), is(1));
        assertThat(synchroniserLogItems.get(0).print(), is("1234-5678: Success"));
    }

    private void verifyCallDurationMeasureInReportDB(String callId) {
        List<CallDurationMeasure> callDurationMeasures = template.loadAll(CallDurationMeasure.class);
        CallDurationMeasure callDurationMeasureFromDB = null;
        for (CallDurationMeasure callDurationMeasure : callDurationMeasures)
            if (callDurationMeasure.getCallId().equals(callId))
                callDurationMeasureFromDB = callDurationMeasure;
        assertNotNull(callDurationMeasureFromDB);
    }


}
