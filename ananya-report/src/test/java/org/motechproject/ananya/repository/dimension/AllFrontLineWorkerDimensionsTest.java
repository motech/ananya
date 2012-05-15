package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

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
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(msisdn, operator, circle, name, designation, status);

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

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(msisdn, "operator1", circle, "name1", "designation1", "status1");

        assertEquals("name1", frontLineWorkerDimension.getName());
        assertEquals("operator1", frontLineWorkerDimension.getOperator());
        assertEquals("status1", frontLineWorkerDimension.getStatus());
        assertEquals("designation1", frontLineWorkerDimension.getDesignation());
    }


    @Test
    public void shouldReturnNullWhenFrontLineWorkerDimensionDoesNotExists() {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(100L);
        assertNull(frontLineWorkerDimension);
    }

    @Test
    public void shouldGetAllUnRegisteredFrontLineWorkerDimensions(){
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
}
