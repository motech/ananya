package org.motechproject.ananya.repository.dimension;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.springframework.beans.factory.annotation.Autowired;

public class AllFrontLineWorkerDimensionsIT extends SpringIntegrationTest {

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    private UUID flwId = UUID.randomUUID();

    @Before
    @After
    public void After() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
    }

    @Test
    public void shouldFetchFrontLineWorkerDimensionIfExists() {
        long msisdn = 9880099L;
        Long alternateContactNumber = 9880098L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String designation = Designation.AWW.name();
        template.save(new FrontLineWorkerDimension(msisdn, alternateContactNumber, operator, "circle", name, designation, status, flwId, null));
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);

        assertEquals(alternateContactNumber, frontLineWorkerDimension.getAlternateContactNumber());
        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(operator, frontLineWorkerDimension.getOperator());
        assertEquals(status, frontLineWorkerDimension.getStatus());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
        assertEquals(flwId, frontLineWorkerDimension.getFlwId());
    }

    @Test
    public void shouldCreateNewFrontLineWorkerDimensionIfIncomingParametersDoesNotExistInDB() {
        long msisdn = 9880099L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String circle = "circle";
        String designation = Designation.AWW.name();

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(msisdn, null, operator, circle, name, designation, status, flwId, VerificationStatus.OTHER);

        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(operator, frontLineWorkerDimension.getOperator());
        assertEquals(status, frontLineWorkerDimension.getStatus());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
        assertEquals(VerificationStatus.OTHER, frontLineWorkerDimension.getVerificationStatus());
        assertEquals(flwId, frontLineWorkerDimension.getFlwId());
    }

    @Test
    public void shouldUpdateFrontLineWorkerDimensionIfIncomingParametersDifferFromDB() {
        long msisdn = 9880099L;
        Long alternateContactNumber = 9880098L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String designation = "designation";
        String circle = "circle";
        template.save(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));

        allFrontLineWorkerDimensions.createOrUpdate(msisdn, alternateContactNumber, "operator1", "newCircle", "name1", "designation1", "status1", flwId, VerificationStatus.OTHER);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);
        assertEquals(alternateContactNumber, frontLineWorkerDimension.getAlternateContactNumber());
        assertEquals("name1", frontLineWorkerDimension.getName());
        assertEquals("operator1", frontLineWorkerDimension.getOperator());
        assertEquals("status1", frontLineWorkerDimension.getStatus());
        assertEquals("designation1", frontLineWorkerDimension.getDesignation());
        assertEquals("newCircle", frontLineWorkerDimension.getCircle());
        assertEquals(VerificationStatus.OTHER, frontLineWorkerDimension.getVerificationStatus());
        assertEquals(flwId, frontLineWorkerDimension.getFlwId());
    }


    @Test
    public void shouldReturnNullWhenFrontLineWorkerDimensionDoesNotExists() {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(100L);
        assertNull(frontLineWorkerDimension);
    }

    @Test
    public void shouldGetAllUnRegisteredFrontLineWorkerDimensions() {
        long msisdn = 9880099L;
        long msisdn1 = 98800999L;
        String name = "name";
        String operator = "operator";
        String status = RegistrationStatus.UNREGISTERED.name();
        String designation = "designation";
        String circle = "Bihar";
        template.save(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        template.save(new FrontLineWorkerDimension(msisdn1, null, operator, circle, name, designation, status, UUID.randomUUID(), null));

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getAllUnregistered();

        assertEquals(2, frontLineWorkerDimensions.size());
    }

    @Test
    public void shouldGetAllTheFrontLineWorkerBasedOnSingleCriteria() {
        Long msisdn = 9880099L;
        Long msisdn1 = 98800999L;
        String airtel = "airtel";
        String bsnl = "bsnl";
        Designation anganwadi = Designation.ANM;
        Designation aww = Designation.AWW;
        String bihar = "Bihar";
        String up = "UP";
        String name2 = "Name2";
        template.save(new FrontLineWorkerDimension(msisdn, null, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name(), flwId, null));
        template.save(new FrontLineWorkerDimension(msisdn1, null, bsnl, up, name2, aww.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null));

        ArrayList<Long> allFilteredMsisdns = new ArrayList<Long>();
        allFilteredMsisdns.add(msisdn);
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(allFilteredMsisdns, msisdn, null, null, null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), null, null, RegistrationStatus.PARTIALLY_REGISTERED.name(), null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED.name(), frontLineWorkerDimensions.get(0).getStatus());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), null, "name2", null, null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(name2, frontLineWorkerDimensions.get(0).getName());
        assertEquals(msisdn1, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), msisdn, null, null, anganwadi.name(), null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(anganwadi.name(), frontLineWorkerDimensions.get(0).getDesignation());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), null, null, null, null, "Bsnl", null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(bsnl, frontLineWorkerDimensions.get(0).getOperator());
        assertEquals(msisdn1, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), null, null, null, null, null, "up");

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(up, frontLineWorkerDimensions.get(0).getCircle());
        assertEquals(msisdn1, frontLineWorkerDimensions.get(0).getMsisdn());
    }

    @Test
    public void shouldGetAllTheFrontLineWorkerBasedOnMultipleCriteria() {
        long msisdn = 9880099L;
        long msisdn1 = 98800999L;
        long msisdn2 = 8800999L;
        String airtel = "airtel";
        String bsnl = "bsnl";
        Designation anganwadi = Designation.ANM;
        Designation anm = Designation.ANM;
        String bihar = "Bihar";
        String up = "UP";
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(msisdn1, null, bsnl, up, "name2", anm.name(), RegistrationStatus.REGISTERED.name(), flwId, null);
        FrontLineWorkerDimension frontLineWorkerDimension2 = new FrontLineWorkerDimension(msisdn2, null, bsnl, up, "name3", anm.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        template.save(new FrontLineWorkerDimension(msisdn, null, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name(), UUID.randomUUID(), null));
        template.save(frontLineWorkerDimension1);
        template.save(frontLineWorkerDimension2);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(new ArrayList<Long>(), null, null, RegistrationStatus.REGISTERED.name(), Designation.ANM.name(), null, up);

        assertEquals(2, frontLineWorkerDimensions.size());
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension1));
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension2));
    }

    @Test
    public void shouldGetAllTheFrontLineWorkerBasedOnMultipleCriteriaAndBasedOnMsisdnList() {
        long msisdn = 9880099L;
        long msisdn1 = 98800999L;
        long msisdn2 = 8800999L;
        String airtel = "airtel";
        String bsnl = "bsnl";
        Designation anganwadi = Designation.ANM;
        Designation anm = Designation.ANM;
        String bihar = "Bihar";
        String up = "UP";
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(msisdn1, null, bsnl, up, "name2", anm.name(), RegistrationStatus.REGISTERED.name(), flwId, null);
        FrontLineWorkerDimension frontLineWorkerDimension2 = new FrontLineWorkerDimension(msisdn2, null, bsnl, up, "name3", anm.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        template.save(new FrontLineWorkerDimension(msisdn, null, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name(), UUID.randomUUID(), null));
        template.save(frontLineWorkerDimension1);
        template.save(frontLineWorkerDimension2);
        ArrayList<Long> allFilteredMsisdns = new ArrayList<Long>();
        allFilteredMsisdns.add(msisdn);
        allFilteredMsisdns.add(msisdn2);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(allFilteredMsisdns, null, null, RegistrationStatus.REGISTERED.name(), null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension2));
    }

    @Test
    public void shouldGetAllTheFrontLineWorkerBasedOnMultipleCriteriaAndBasedOnMsisdnListAndBasedOnMsisdn() {
        long msisdn = 9880099L;
        long msisdn1 = 98800999L;
        long msisdn2 = 8800999L;
        String airtel = "airtel";
        String bsnl = "bsnl";
        Designation anganwadi = Designation.ANM;
        Designation anm = Designation.ANM;
        String bihar = "Bihar";
        String up = "UP";
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(msisdn1, null, bsnl, up, "name2", anm.name(), RegistrationStatus.REGISTERED.name(),flwId, null);
        FrontLineWorkerDimension frontLineWorkerDimension2 = new FrontLineWorkerDimension(msisdn2, null, bsnl, up, "name3", anm.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        template.save(new FrontLineWorkerDimension(msisdn, null, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name(),UUID.randomUUID(), null));
        template.save(frontLineWorkerDimension1);
        template.save(frontLineWorkerDimension2);
        ArrayList<Long> allFilteredMsisdns = new ArrayList<Long>();
        allFilteredMsisdns.add(msisdn1);
        allFilteredMsisdns.add(msisdn2);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(allFilteredMsisdns, msisdn1, null, null, null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension1));
    }

    @Test
    public void shouldGetAllFrontLineWorkers_WhichMatchesPartialName() {

        long msisdn1 = 90909009L;
        FrontLineWorkerDimension flw1 = new FrontLineWorkerDimension(msisdn1, null, "Airtel", "UO",
                "Ramakrishna", Designation.ANM.name(), RegistrationStatus.REGISTERED.name(), flwId, null);
        long msisdn2 = 90909002L;
        FrontLineWorkerDimension flw2 = new FrontLineWorkerDimension(msisdn2, null, "Airtel", "UO",
                "Krishnan", Designation.ANM.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        long msisdn3 = 90909003L;
        FrontLineWorkerDimension flw3 = new FrontLineWorkerDimension(msisdn3, null, "Airtel", "UO",
                "Ramana", Designation.ANM.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);

        template.save(flw1);
        template.save(flw2);
        template.save(flw3);

        List<FrontLineWorkerDimension> filteredFlws = allFrontLineWorkerDimensions.getFilteredFLWFor(
                Arrays.asList(msisdn1, msisdn2, msisdn3), null, "krish", RegistrationStatus.REGISTERED.name(), Designation.ANM.name(), "Airtel", "UO");

        assertThat(filteredFlws.size(), is(2));
        assertThat(filteredFlws, hasItem(flw1));
        assertThat(filteredFlws, hasItem(flw2));
        assertThat(filteredFlws, not(hasItem(flw3)));
    }

    @Test
    public void shouldGetAllFrontLineWorkers_WhereThereAreNoDesignation() {

        long msisdn1 = 90909009L;
        FrontLineWorkerDimension flw1 = new FrontLineWorkerDimension(msisdn1, null, "Airtel", "UO",
                "Ramakrishna", null, RegistrationStatus.REGISTERED.name(), flwId, null);
        long msisdn2 = 90909002L;
        FrontLineWorkerDimension flw2 = new FrontLineWorkerDimension(msisdn2, null, "Airtel", "UO",
                "Krishnan", null, RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);
        long msisdn3 = 90909003L;
        FrontLineWorkerDimension flw3 = new FrontLineWorkerDimension(msisdn3, null, "Airtel", "UO",
                "Ramana", Designation.ANM.name(), RegistrationStatus.REGISTERED.name(), UUID.randomUUID(), null);

        template.save(flw1);
        template.save(flw2);
        template.save(flw3);

        List<FrontLineWorkerDimension> filteredFlws = allFrontLineWorkerDimensions.getFilteredFLWFor(
                new ArrayList<Long>(), null, null, null, "null", null, null);

        assertThat(filteredFlws.size(), is(2));
        assertThat(filteredFlws, hasItem(flw1));
        assertThat(filteredFlws, hasItem(flw2));
        assertThat(filteredFlws, not(hasItem(flw3)));
    }

    @Test
    public void shouldSaveAllFLW() {
        Long msisdn1 = 90909009L;
        Long alternateContactNumber = 90909009L;
        FrontLineWorkerDimension flw1 = new FrontLineWorkerDimension(msisdn1, alternateContactNumber, "Airtel", "UO",
                "Ramakrishna", null, RegistrationStatus.REGISTERED.name(), flwId, null);
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<>();
        frontLineWorkerDimensions.add(flw1);

        allFrontLineWorkerDimensions.createOrUpdateAll(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> actualFLWs = template.loadAll(FrontLineWorkerDimension.class);
        assertEquals(1, actualFLWs.size());
        assertEquals(msisdn1, actualFLWs.get(0).getMsisdn());
        assertEquals(alternateContactNumber, actualFLWs.get(0).getAlternateContactNumber());
    }
}
