package org.motechproject.ananya.support.synchroniser;


import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLogItem;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
@Ignore
public class CallDurationSynchroniserIT {

    @Autowired
    private CallDurationSychroniser callDurationSychroniser;
    @Autowired
    private AllCallLogs allCallLogs;
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Before
    public void setUp() {
        resetDB();
    }

    @After
    public void tearDown() {
        resetDB();
    }

    private void resetDB() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
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

        SynchroniserLog synchroniserLog = callDurationSychroniser.replicate();

        verifyCallDurationMeasureInReportDB(callId);
        verifySynchroniserLog(synchroniserLog);
        assertTrue(allCallLogs.getAll().isEmpty());
    }

    private void setUpReportData(String callerId, String callId) {
        CallLog callLog = new CallLog(callId, callerId,"321");
        callLog.addItem(new CallLogItem(CallFlowType.CALL, DateUtil.now(), DateUtil.now().plusSeconds(20)));
        allCallLogs.add(callLog);
    }

    private void setUpTransactionData(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId),
                "airtel", "bihar", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString(), UUID.randomUUID());
        template.save(frontLineWorkerDimension);
        LocationDimension locationDimension = new LocationDimension("locationId", "district", "block", "panchayat", "VALID");
        template.save(locationDimension);
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        template.save(timeDimension);
        template.save(new RegistrationMeasure(frontLineWorkerDimension,locationDimension,timeDimension, ""));

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
