package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerMapperTest {
    @Test
    public void shouldMapFromRequestToFLW() {
        String msisdn = "123457890";
        String name = "name";
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String circle = "bihar";
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, operator, circle, new LocationRequest(district, block, panchayat));

        FrontLineWorker frontLineWorker = FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest);

        assertEquals(msisdn, frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation().name());
        assertEquals(circle, frontLineWorker.getCircle());
        assertEquals(operator, frontLineWorker.getOperator());
    }

    @Test
    public void shouldMapFromDimensionToResponse() {
        Long msisdn = 1234567890L;
        String operator = "airtel";
        String circle = "bihar";
        String name = "name";
        String designation = Designation.ANM.name();
        String status = RegistrationStatus.REGISTERED.name();

        FrontLineWorkerResponse frontLineWorkerResponse = FrontLineWorkerMapper.mapFrom(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));

        assertEquals(msisdn.toString(), frontLineWorkerResponse.getMsisdn());
        assertEquals(name, frontLineWorkerResponse.getName());
        assertEquals(circle, frontLineWorkerResponse.getCircle());
        assertEquals(operator, frontLineWorkerResponse.getOperator());
        assertEquals(status, frontLineWorkerResponse.getStatus());
    }
}
