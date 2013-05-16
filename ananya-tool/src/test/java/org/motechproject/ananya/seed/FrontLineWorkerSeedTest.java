package org.motechproject.ananya.seed;

import junit.framework.Assert;
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
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;

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

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private LocationSeed locationSeed;

    @Autowired
    private TimeSeed timeSeed;

    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;

    @Qualifier("testDataAccessTemplate")
    @Autowired
    private TestDataAccessTemplate template;

    @Autowired
    private EhCacheManagerFactoryBean defaultEhCacheManager;

    @Autowired
    private CacheAspectSupport cacheAspectSupport;

    @Before
    public void setUp() throws IOException {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(defaultEhCacheManager.getObject());
        cacheAspectSupport.setCacheManager(cacheManager);

        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        allFrontLineWorkers.removeAll();
        allFrontLineWorkerDimensions.removeAll();
        locationSeed.loadLocationsFromCSVFile();
        timeSeed.createDimensionsInPostgres();
        allTimeDimensions.getFor(DateTime.now());
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
        allFrontLineWorkers.removeAll();
        allFrontLineWorkerDimensions.removeAll();
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
    }

    @Test
    public void shouldRegisterFrontLineWorkersThroughTheFrontLineWorkerSeed() throws IOException {
        frontLineWorkerSeed.createFrontlineWorkersFromCSVFile();

        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        int numberOfCsvEntries = 13;
        assertEquals(numberOfCsvEntries, frontLineWorkers.size());
        FrontLineWorker frontLineWorker = frontLineWorkers.get(1);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        Assert.assertEquals(frontLineWorkerDimension.getName(), frontLineWorker.name());
        Assert.assertEquals(frontLineWorkerDimension.getMsisdn(), frontLineWorker.msisdn());
        Assert.assertEquals(frontLineWorkerDimension.getOperator(), frontLineWorker.getOperator());
        assertNotNull(frontLineWorker.getFlwId());
        Assert.assertEquals(frontLineWorkerDimension.getFlwId(), frontLineWorker.getFlwId());
        FrontLineWorker flwWithDummyFlwId = allFrontLineWorkers.findByMsisdn("916756453423");
        assertEquals("11111111-1111-1111-1111-111111111111", flwWithDummyFlwId.getFlwId().toString());
    }

    @Test
    public void shouldUpdateStatusOfNewlyRegisteredToUnregistered() {
        RegistrationStatus registrationStatus = RegistrationStatus.UNREGISTERED;
        Designation designation = Designation.ASHA;
        String name = "Name";
        String language= "language";
        Long msisdn = 919986574410l;
        FrontLineWorker frontLineWorkerCreated = new FrontLineWorker(msisdn.toString(), name, designation, new Location(), language, null, UUID.randomUUID());
        template.save(new FrontLineWorkerDimension(msisdn, "Airtel", "Bihar", name, designation.name(), registrationStatus.name(), frontLineWorkerCreated.getFlwId(), null));
        allFrontLineWorkers.add(frontLineWorkerCreated);

        frontLineWorkerSeed.updateRegistrationStatusOfFrontLineWorkersRegisteredViaCalls();

        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn.toString());
        assertEquals(RegistrationStatus.UNREGISTERED, frontLineWorker.getStatus());
    }

    @Test
    @Ignore
    public void shouldUpdateOperatorInReportDbIfTheOperatorIsPresentInCouchDb() {
        RegistrationStatus registrationStatus = RegistrationStatus.UNREGISTERED;
        Designation designation = Designation.ASHA;
        String name = "Name";
        String language= "language";
        Long msisdn = 1234567890L;
        Long correctMsisdn = 911234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn.toString(), name, designation, Location.getDefaultLocation(), language, null, UUID.randomUUID());
        String operator = "Airtel";
        frontLineWorker.setOperator(operator);
        ReflectionTestUtils.setField(frontLineWorker, "msisdn", msisdn.toString());
        allFrontLineWorkers.add(frontLineWorker);

        template.save(new FrontLineWorkerDimension(msisdn, null, "Bihar", name, designation.name(), registrationStatus.name(), frontLineWorker.getFlwId(), null));

        frontLineWorkerSeed.updateCorrectCallerIdsCircleOperatorAndDesignation();

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(correctMsisdn);
        assertNotNull(frontLineWorkerDimension);
        assertEquals(operator, frontLineWorkerDimension.getOperator());
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();

        for (FrontLineWorker flw : frontLineWorkers) {
            String defaultCircle = "BIHAR";
            assertEquals(defaultCircle, flw.getCircle());
            assertEquals(12, flw.getMsisdn().length());
        }
    }

    private FrontLineWorkerDimension getFLWDimensionFromFLW(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerDimension(frontLineWorker.msisdn(),
                frontLineWorker.getOperator(), frontLineWorker.getCircle(), frontLineWorker.getName(),
                frontLineWorker.designationName(), frontLineWorker.getStatus().toString(), frontLineWorker.getFlwId(), null);
    }

    private FrontLineWorker getFrontLineWorker(String msisdn, String operator, RegistrationStatus registrationStatus, Location location) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, operator, "", "");
        frontLineWorker.setRegistrationStatus(registrationStatus);
        if (location != null) frontLineWorker.setLocation(location);
        return frontLineWorker;
    }

    private String getFLWDimensionStatus(FrontLineWorker frontLineWorker) {
        return allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn()).getStatus();
    }

    @Test
    public void shouldActivateNewRegistrationStatusesForAllFLWs() {
        Location location = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, null, null);
        allLocations.add(location);

        FrontLineWorker notCalledPartialFLW = getFrontLineWorker("9999991", null, RegistrationStatus.PARTIALLY_REGISTERED, null);
        FrontLineWorker notCalledRegisteredFLW = getFrontLineWorker("9999993", "", RegistrationStatus.REGISTERED, location);
        FrontLineWorker calledUnregisteredFLW = getFrontLineWorker("9999994", "airtel", RegistrationStatus.UNREGISTERED, null);
        FrontLineWorker calledRegisteredFLW = getFrontLineWorker("9999995", "airtel", RegistrationStatus.REGISTERED, location);
        calledRegisteredFLW.setName("name");
        calledRegisteredFLW.setDesignation(Designation.ANM);
        FrontLineWorker calledPartialFLW = getFrontLineWorker("9999996", "airtel", RegistrationStatus.PARTIALLY_REGISTERED, null);

        allFrontLineWorkers.add(notCalledPartialFLW);
        allFrontLineWorkers.add(notCalledRegisteredFLW);
        allFrontLineWorkers.add(calledUnregisteredFLW);
        allFrontLineWorkers.add(calledRegisteredFLW);
        allFrontLineWorkers.add(calledPartialFLW);

        FrontLineWorkerDimension notCalledPartialFLWDimension = getFLWDimensionFromFLW(notCalledPartialFLW);
        FrontLineWorkerDimension notCalledRegisteredFLWDimension = getFLWDimensionFromFLW(notCalledRegisteredFLW);
        FrontLineWorkerDimension calledUnregisteredFLWDimension = getFLWDimensionFromFLW(calledUnregisteredFLW);
        FrontLineWorkerDimension calledRegisteredFLWDimension = getFLWDimensionFromFLW(calledRegisteredFLW);
        FrontLineWorkerDimension calledPartialFLWDimension = getFLWDimensionFromFLW(calledPartialFLW);

        template.save(notCalledPartialFLWDimension);
        template.save(notCalledRegisteredFLWDimension);
        template.save(calledUnregisteredFLWDimension);
        template.save(calledRegisteredFLWDimension);
        template.save(calledPartialFLWDimension);

        frontLineWorkerSeed.activateNewRegistrationStatusesForAllFLWs();

        notCalledPartialFLW = allFrontLineWorkers.findByMsisdn(notCalledPartialFLW.getMsisdn());
        notCalledRegisteredFLW = allFrontLineWorkers.findByMsisdn(notCalledRegisteredFLW.getMsisdn());
        calledUnregisteredFLW = allFrontLineWorkers.findByMsisdn(calledUnregisteredFLW.getMsisdn());
        calledRegisteredFLW = allFrontLineWorkers.findByMsisdn(calledRegisteredFLW.getMsisdn());
        calledPartialFLW = allFrontLineWorkers.findByMsisdn(calledPartialFLW.getMsisdn());

        assertEquals(RegistrationStatus.UNREGISTERED, notCalledPartialFLW.getStatus());
        assertEquals(RegistrationStatus.UNREGISTERED, notCalledRegisteredFLW.getStatus());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, calledUnregisteredFLW.getStatus());
        assertEquals(RegistrationStatus.REGISTERED, calledRegisteredFLW.getStatus());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, calledPartialFLW.getStatus());

        assertEquals(RegistrationStatus.UNREGISTERED.toString(), getFLWDimensionStatus(notCalledPartialFLW));
        assertEquals(RegistrationStatus.UNREGISTERED.toString(), getFLWDimensionStatus(notCalledRegisteredFLW));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED.toString(), getFLWDimensionStatus(calledUnregisteredFLW));
        assertEquals(RegistrationStatus.REGISTERED.toString(), getFLWDimensionStatus(calledRegisteredFLW));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED.toString(), getFLWDimensionStatus(calledPartialFLW));
    }

    @Test
    public void shouldCorrectInvalidDesignationsInCouchAndPostgresDB() throws IOException {
    	String language= "language";
        FrontLineWorker flwInCSVWithInvalidDesignation1 = new FrontLineWorker("9000000001", "",
                Designation.valueOf("INVALID"), Location.getDefaultLocation(), language, null, UUID.randomUUID());
        FrontLineWorker flwInCSVWithInvalidDesignation2 = new FrontLineWorker("9000000002", "",
                Designation.valueOf("INVALID"), Location.getDefaultLocation(), language, null, UUID.randomUUID());
        FrontLineWorker flwInCSVWithInvalidDesignation3 = new FrontLineWorker("9000000003", "",
                Designation.valueOf("INVALID"), Location.getDefaultLocation(), language, null, UUID.randomUUID());
        FrontLineWorker flwInCSVWithInvalidDesignation4 = new FrontLineWorker("9000000004", "",
                Designation.valueOf("INVALID"), Location.getDefaultLocation(), language, null, UUID.randomUUID());
        FrontLineWorker invalidFLW = new FrontLineWorker("9000000005", "",
                Designation.valueOf("INVALID"), Location.getDefaultLocation(), language, null, UUID.randomUUID());

        FrontLineWorkerDimension frontLineWorkerDimension1 = getFLWDimensionFromFLW(flwInCSVWithInvalidDesignation1);
        FrontLineWorkerDimension frontLineWorkerDimension2 = getFLWDimensionFromFLW(flwInCSVWithInvalidDesignation2);
        FrontLineWorkerDimension frontLineWorkerDimension3 = getFLWDimensionFromFLW(flwInCSVWithInvalidDesignation3);
        FrontLineWorkerDimension frontLineWorkerDimension4 = getFLWDimensionFromFLW(flwInCSVWithInvalidDesignation4);
        FrontLineWorkerDimension frontLineWorkerDimension5 = getFLWDimensionFromFLW(invalidFLW);

        allFrontLineWorkers.add(flwInCSVWithInvalidDesignation1);
        allFrontLineWorkers.add(flwInCSVWithInvalidDesignation2);
        allFrontLineWorkers.add(flwInCSVWithInvalidDesignation3);
        allFrontLineWorkers.add(flwInCSVWithInvalidDesignation4);
        allFrontLineWorkers.add(invalidFLW);

        template.save(frontLineWorkerDimension1);
        template.save(frontLineWorkerDimension2);
        template.save(frontLineWorkerDimension3);
        template.save(frontLineWorkerDimension4);
        template.save(frontLineWorkerDimension5);

        frontLineWorkerSeed.correctAllDesignations();

        assertEquals(Designation.ANM, allFrontLineWorkers.findByMsisdn("919000000001").getDesignation());
        assertEquals(Designation.AWW, allFrontLineWorkers.findByMsisdn("919000000002").getDesignation());
        assertEquals(null, allFrontLineWorkers.findByMsisdn("919000000003").getDesignation());
        assertEquals(null, allFrontLineWorkers.findByMsisdn("919000000004").getDesignation());
        assertEquals(null, allFrontLineWorkers.findByMsisdn("919000000005").getDesignation());

        assertEquals("ANM", allFrontLineWorkerDimensions.fetchFor(919000000001L).getDesignation());
        assertEquals("AWW", allFrontLineWorkerDimensions.fetchFor(919000000002L).getDesignation());
        assertEquals(null, allFrontLineWorkerDimensions.fetchFor(919000000003L).getDesignation());
        assertEquals(null, allFrontLineWorkerDimensions.fetchFor(919000000004L).getDesignation());
        assertEquals(null, allFrontLineWorkerDimensions.fetchFor(919000000005L).getDesignation());
    }

    @Test
    public void shouldNotCreateDimensionAndMeasureIfFLWAlreadyExistsInPostgres() {
        Location location = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, null, null);
        allLocations.add(location);

        FrontLineWorker frontLineWorker = getFrontLineWorker("9999991", null, RegistrationStatus.PARTIALLY_REGISTERED, null);
        frontLineWorker.setVerificationStatus(VerificationStatus.OTHER);
        allFrontLineWorkers.add(frontLineWorker);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(
                frontLineWorker.msisdn(),
                frontLineWorker.getOperator(),
                frontLineWorker.getCircle(),
                frontLineWorker.name(),
                frontLineWorker.designationName(),
                frontLineWorker.getStatus().toString(),
                frontLineWorker.getFlwId(), VerificationStatus.OTHER);

        frontLineWorkerSeed.createDimensionAndRegistrationMeasureForMissingFLWs();

        FrontLineWorkerDimension frontLineWorkerDimensionFromDb = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        assertEquals(frontLineWorkerDimension.getId(), frontLineWorkerDimensionFromDb.getId());

        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        assertNull(registrationMeasure);
    }

    @Test
    public void shouldCreateDimensionAndMeasureIfFLWNotInPostgres() {
        Location location = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, null, null);
        allLocations.add(location);

        FrontLineWorker frontLineWorker = getFrontLineWorker("9999991", null, RegistrationStatus.PARTIALLY_REGISTERED, null);
        frontLineWorker.setVerificationStatus(VerificationStatus.OTHER);
        allFrontLineWorkers.add(frontLineWorker);

        frontLineWorkerSeed.createDimensionAndRegistrationMeasureForMissingFLWs();

        FrontLineWorkerDimension frontLineWorkerDimensionFromDb = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        assertNotNull(frontLineWorkerDimensionFromDb);
        assertEquals(VerificationStatus.OTHER, frontLineWorkerDimensionFromDb.getVerificationStatus());

        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimensionFromDb.getId());
        assertNotNull(registrationMeasure);
    }

}
