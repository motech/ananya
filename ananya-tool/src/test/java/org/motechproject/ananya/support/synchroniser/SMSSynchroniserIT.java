package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLogItem;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class SMSSynchroniserIT {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllSMSReferences allSMSReferences;

    @Autowired
    private SMSSynchroniser smsSynchroniser;

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
        template.deleteAll(template.loadAll(SMSSentMeasure.class));
        allFrontLineWorkers.removeAll();
        allSMSReferences.removeAll();
    }

    @Test
    public void shouldMoveSMSDataFromTransactionDbToReportDb() {
        String callerId = "1234";
        DateTime callStartTime = DateUtil.now();
        DateTime fromDate = DateUtil.now();
        DateTime toDate = fromDate.plusHours(8);
        String operator = "airtel";

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(Long.valueOf(callerId), operator, "circle", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString());
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(callStartTime);
        LocationDimension locationDimension = allLocationDimensions.add(new LocationDimension("locationId","district","block","panchayat"));

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.incrementCertificateCourseAttempts();
        allFrontLineWorkers.add(frontLineWorker);

        template.save(new RegistrationMeasure(frontLineWorkerDimension,locationDimension,timeDimension));

        SMSReference smsReference = new SMSReference(callerId, frontLineWorker.getId());
        smsReference.add("S00V00", 1);
        allSMSReferences.add(smsReference);

        SynchroniserLog synchroniserLog = smsSynchroniser.replicate(fromDate, toDate);

        verifySMSSentMeasureInReportDb(callerId);
        verifySynchroniserLog(synchroniserLog);
    }

    private void verifySynchroniserLog(SynchroniserLog synchroniserLog) {
        List<SynchroniserLogItem> synchroniserLogItems = synchroniserLog.getItems();
        assertThat(synchroniserLogItems.size(), is(1));
        assertThat(synchroniserLogItems.get(0).print(), is("1234: Success"));
    }

    private void verifySMSSentMeasureInReportDb(String callerId) {
        List<SMSSentMeasure> smsSentMeasures = template.loadAll(SMSSentMeasure.class);
        SMSSentMeasure smsSentMeasureToMatch = null;
        for (SMSSentMeasure smsSentMeasure : smsSentMeasures)
            if (smsSentMeasure.getFrontLineWorkerDimension().getMsisdn().equals(Long.valueOf(callerId))
                    && smsSentMeasure.getCourseAttempt() == 1
                    && smsSentMeasure.getSmsReferenceNumber().equals("S00V00"))
                smsSentMeasureToMatch = smsSentMeasure;
        assertNotNull(smsSentMeasureToMatch);
    }
}
