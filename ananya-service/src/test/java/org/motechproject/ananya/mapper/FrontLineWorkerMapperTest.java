package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.response.FrontLineWorkerResponse;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerMapperTest {

    @Test
    public void shouldMapFromDimensionToResponse() {
        Long msisdn = 1234567890L;
        String operator = "airtel";
        String circle = "bihar";
        String name = "name";
        String designation = Designation.ANM.name();
        String status = RegistrationStatus.REGISTERED.name();

        FrontLineWorkerResponse frontLineWorkerResponse = FrontLineWorkerMapper.mapFrom(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, UUID.randomUUID()));

        assertEquals(msisdn.toString(), frontLineWorkerResponse.getMsisdn());
        assertEquals(name, frontLineWorkerResponse.getName());
        assertEquals(circle, frontLineWorkerResponse.getCircle());
        assertEquals(operator, frontLineWorkerResponse.getOperator());
        assertEquals(status, frontLineWorkerResponse.getStatus());
    }
}
