package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLogItem;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class FrontLineWorkerSynchroniserIT {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private LocationRegistrationService locationRegistrationService;
    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private FrontLineWorkerSynchroniser frontLineWorkerSynchroniser;
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;
    @Autowired
    private AllLocations allLocations;

    @Before
    public void setUp() {
        resetDB();
    }

    @After
    public void tearDown() {
        resetDB();
    }

    private void resetDB() {
        allFrontLineWorkers.removeAll();
        allLocations.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldMigrateFLWDataFromTransactionDbToReportDb() {
        DateTime fromDate = DateUtil.now();
        DateTime toDate = DateUtil.now().plusDays(2);

        locationRegistrationService.loadDefaultLocation();
        setUpTestFLW("111", fromDate);
        setUpTestFLW("222", fromDate.plusDays(1));
        setUpTestFLW("333", fromDate.plusDays(2));
        setUpTestFLW("444", fromDate.plusDays(3));

        SynchroniserLog synchroniserLog = frontLineWorkerSynchroniser.replicate(fromDate, toDate);

        verifyFLWExistsInReportDbFor("111");
        verifyFLWExistsInReportDbFor("222");
        verifyFLWExistsInReportDbFor("333");
        verifyFLWNotExistsInReportDbFor("444");
        verifySynchroniserLog(synchroniserLog);

    }

    private void setUpTestFLW(String msisdn, DateTime registeredDate) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.setRegisteredDate(registeredDate);
        allFrontLineWorkers.add(frontLineWorker);
        allTimeDimensions.addOrUpdate(registeredDate);

    }

    private void verifyFLWExistsInReportDbFor(String msisdn) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
        assertNotNull(frontLineWorkerDimension);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        assertNotNull(registrationMeasure);
    }

    private void verifyFLWNotExistsInReportDbFor(String msisdn) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
        assertNull(frontLineWorkerDimension);
    }

    private void verifySynchroniserLog(SynchroniserLog synchroniserLog) {
        List<SynchroniserLogItem> synchroniserLogItems = synchroniserLog.getItems();
        assertThat(synchroniserLogItems.size(), is(3));
        assertThat(synchroniserLogItems.get(0).print(), is("111: Success"));
        assertThat(synchroniserLogItems.get(1).print(), is("222: Success"));
        assertThat(synchroniserLogItems.get(2).print(), is("333: Success"));
    }

}
