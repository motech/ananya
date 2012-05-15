package org.motechproject.ananya.seed;

import junit.framework.Assert;
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
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class FrontLineWorkerSeedTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    private FrontLineWorkerSeed frontLineWorkerSeed;

    @Autowired
    private AllLocations allLocations;

    @Qualifier("testDataAccessTemplate")
    @Autowired
    private TestDataAccessTemplate template;

    @Autowired
    private LocationSeed locationSeed;

    @Autowired
    private TimeSeed timeSeed;

    @Before
    public void setUp() throws IOException {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        allFrontLineWorkers.removeAll();
        allFrontLineWorkerDimensions.removeAll();
        locationSeed.load();
        timeSeed.load();
    }

    @Test
    public void shouldRegisterFrontLineWorkersThroughTheFrontLineWorkerSeed() throws IOException {
        frontLineWorkerSeed.load();

        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        assertEquals(6, frontLineWorkers.size());
        FrontLineWorker frontLineWorker = frontLineWorkers.get(1);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        Assert.assertEquals(frontLineWorkerDimension.getName(), frontLineWorker.name());
        Assert.assertEquals(frontLineWorkerDimension.getMsisdn(), frontLineWorker.msisdn());
        Assert.assertEquals(frontLineWorkerDimension.getOperator(), frontLineWorker.getOperator());
    }

    @Test
    public void shouldUpdateStatusOfNewlyRegisteredToUnregistered() {
        RegistrationStatus registrationStatus = RegistrationStatus.UNREGISTERED;
        Designation designation = Designation.ASHA;
        String name = "Name";
        Long msisdn = 123L;
        template.save(new FrontLineWorkerDimension(msisdn, "Airtel", "Bihar", name, designation.name(), registrationStatus.name()));
        allFrontLineWorkers.add(new FrontLineWorker(msisdn.toString(), name, designation, new Location(), RegistrationStatus.PARTIALLY_REGISTERED));

        frontLineWorkerSeed.updateStatusOfNewlyRegistered();

        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn.toString());
        assertEquals(RegistrationStatus.UNREGISTERED, frontLineWorker.status());
    }

    @Test
    public void shouldUpdateOperatorInReportDbIfTheOperatorIsPresentInCouchDb() {
        RegistrationStatus registrationStatus = RegistrationStatus.UNREGISTERED;
        Designation designation = Designation.ASHA;
        String name = "Name";
        Long msisdn = 123L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn.toString(), name, designation, Location.getDefaultLocation(), registrationStatus);
        String operator = "Airtel";
        frontLineWorker.setOperator(operator);
        allFrontLineWorkers.add(frontLineWorker);
        template.save(new FrontLineWorkerDimension(msisdn, null, "Bihar", name, designation.name(), registrationStatus.name()));

        frontLineWorkerSeed.updateOperatorInReportDbFromCouchdb();

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);
        assertEquals(operator, frontLineWorkerDimension.getOperator());
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        allFrontLineWorkers.removeAll();
        allFrontLineWorkerDimensions.removeAll();
    }
}
