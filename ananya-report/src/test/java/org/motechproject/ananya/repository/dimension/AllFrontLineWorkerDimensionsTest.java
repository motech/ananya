package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.*;

public class AllFrontLineWorkerDimensionsTest extends SpringIntegrationTest {

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @After
    public void After() {
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
    }

    @Test
    public void shouldFetchFrontLineWorkerDimensionIfExists() {
        long msisdn = 9880099L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String designation = Designation.ANGANWADI.name();
        template.save(new FrontLineWorkerDimension(msisdn, operator, "circle", name, designation, status));
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);

        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(operator, frontLineWorkerDimension.getOperator());
        assertEquals(status, frontLineWorkerDimension.getStatus());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
    }

    @Test
    public void shouldCreateNewFrontLineWorkerDimensionIfIncomingParametersDoesNotExistInDB() {
        long msisdn = 9880099L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String circle = "circle";
        String designation = Designation.ANGANWADI.name();
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(msisdn, operator, circle, name, designation, status);

        assertEquals(name, frontLineWorkerDimension.getName());
        assertEquals(operator, frontLineWorkerDimension.getOperator());
        assertEquals(status, frontLineWorkerDimension.getStatus());
        assertEquals(designation, frontLineWorkerDimension.getDesignation());
    }


    @Test
    public void shouldUpdateFrontLineWorkerDimensionIfIncomingParametersDifferFromDB() {
        long msisdn = 9880099L;
        String name = "name";
        String operator = "operator";
        String status = "status";
        String designation = "designation";
        String circle = "circle";
        template.save(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));

        allFrontLineWorkerDimensions.createOrUpdate(msisdn, "operator1", "newCircle", "name1", "designation1", "status1");

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);
        assertEquals("name1", frontLineWorkerDimension.getName());
        assertEquals("operator1", frontLineWorkerDimension.getOperator());
        assertEquals("status1", frontLineWorkerDimension.getStatus());
        assertEquals("designation1", frontLineWorkerDimension.getDesignation());
        assertEquals("newCircle", frontLineWorkerDimension.getCircle());
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
        template.save(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        template.save(new FrontLineWorkerDimension(msisdn1, operator, circle, name, designation, status));

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getAllUnregistered();

        assertEquals(2, frontLineWorkerDimensions.size());
    }

    @Test
    public void shouldGetAllTheFrontLineWorkerBasedOnSingleCriteria() {
        Long msisdn = 9880099L;
        Long msisdn1 = 98800999L;
        String airtel = "airtel";
        String bsnl = "bsnl";
        Designation anganwadi = Designation.ANGANWADI;
        Designation anm = Designation.ANM;
        String bihar = "Bihar";
        String up = "UP";
        String name2 = "name2";
        template.save(new FrontLineWorkerDimension(msisdn, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name()));
        template.save(new FrontLineWorkerDimension(msisdn1, bsnl, up, name2, anm.name(), RegistrationStatus.REGISTERED.name()));

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(msisdn, null, null, null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, null, RegistrationStatus.PARTIALLY_REGISTERED.name(), null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED.name(), frontLineWorkerDimensions.get(0).getStatus());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, name2, null, null, null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(name2, frontLineWorkerDimensions.get(0).getName());
        assertEquals(msisdn1, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, null, null, anganwadi.name(), null, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(anganwadi.name(), frontLineWorkerDimensions.get(0).getDesignation());
        assertEquals(msisdn, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, null, null, null, bsnl, null);

        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(bsnl, frontLineWorkerDimensions.get(0).getOperator());
        assertEquals(msisdn1, frontLineWorkerDimensions.get(0).getMsisdn());

        frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, null, null, null, null, up);

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
        Designation anganwadi = Designation.ANGANWADI;
        Designation anm = Designation.ANM;
        String bihar = "Bihar";
        String up = "UP";
        FrontLineWorkerDimension frontLineWorkerDimension1 = new FrontLineWorkerDimension(msisdn1, bsnl, up, "name2", anm.name(), RegistrationStatus.REGISTERED.name());
        FrontLineWorkerDimension frontLineWorkerDimension2 = new FrontLineWorkerDimension(msisdn2, bsnl, up, "name3", anm.name(), RegistrationStatus.REGISTERED.name());
        template.save(new FrontLineWorkerDimension(msisdn, airtel, bihar, "name1", anganwadi.name(), RegistrationStatus.PARTIALLY_REGISTERED.name()));
        template.save(frontLineWorkerDimension1);
        template.save(frontLineWorkerDimension2);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getFilteredFLWFor(null, null, RegistrationStatus.REGISTERED.name(), Designation.ANM.name(), null, up);

        assertEquals(2, frontLineWorkerDimensions.size());
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension1));
        assertTrue(frontLineWorkerDimensions.contains(frontLineWorkerDimension2));
    }
}
