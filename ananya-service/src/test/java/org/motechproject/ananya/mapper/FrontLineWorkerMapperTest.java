package org.motechproject.ananya.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerMapperTest {
    @Test
    public void shouldMapFromRequestToFLW() {
        String msisdn = "1234567678890 ";
        String name = " name";
        String designation = Designation.ANM.name();
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        DateTime lastModified = new DateTime(2000,11,23,20,25);
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat), lastModified.toDate(), UUID.randomUUID());

        FrontLineWorker frontLineWorker = FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest);

        assertEquals("1234567678890", frontLineWorker.getMsisdn());
        assertEquals("name", frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation().name());
        assertEquals(lastModified, frontLineWorker.getLastModified());
    }

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
