package org.motechproject.ananya.functional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class RegistrationCallFlowTest extends SpringIntegrationTest{

    private CallFlow callFlow;
    private MyWebClient myWebClient;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @Test
    public void shouldRegisterNewFLW() throws IOException {
        MyWebClient.PostParam designation = param("designation", "ASHA");
        String panchayatCode = "S01D001B001V001";
        MyWebClient.PostParam panchayat = param("panchayat", panchayatCode);
        MyWebClient.PostParam callerId = param("session.connection.remote.uri", "9986574420");
        new MyWebClient().post("http://localhost:9979/ananya/flw/register", designation,panchayat ,callerId);

        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("9986574420");
        Location location = allLocations.findByExternalId(panchayatCode);

        assertEquals(location.getId(), frontLineWorker.getLocationId());
    }
    
    @Test
    public void shouldUpdateTranscribedNameAndStatusForExistingWorker() throws IOException {

        allFrontLineWorkers.add(new FrontLineWorker("555", Designation.ANGANWADI, "S01D001").status(RegistrationStatus.PENDING_REGISTRATION));

        String msisdn = "555";
        String name = "hobbes";

        MyWebClient.PostParam msisdnParam = param("msisdn", msisdn);
        MyWebClient.PostParam nameParam = param("name", name);

        new MyWebClient().post("http://localhost:9979/ananya/flw/save/name", msisdnParam, nameParam);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

        assertEquals(updatedFrontLineWorker.getName(), name);
        assertEquals(updatedFrontLineWorker.getStatus(), RegistrationStatus.REGISTERED);
    }
}
