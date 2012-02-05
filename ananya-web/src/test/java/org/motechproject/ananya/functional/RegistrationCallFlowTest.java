package org.motechproject.ananya.functional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class RegistrationCallFlowTest extends SpringIntegrationTest{

    private CallFlow callFlow;
    MyWebClient myWebClient;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;


    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws Exception {
        callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/jobaid/register/");
        for (String record : asList("name", "district", "block", "panchayat")) {
            assertOnRecord(record);
            assertOnRecordConfirm(record);
        }
        assertOnDesignationField();
        assertNonInterActivePrompts();
        assertEquals("/ananya/vxml/jobaid.vxml", callFlow.readString("/vxml/form/block/goto/@next"));
        assertEquals("session.connection.remote.uri designation name district block panchayat", callFlow.readString("/vxml/form/block/data/@namelist"));
    }

    @Test
    public void shouldRegisterNewFLW() throws IOException {
        MyWebClient.PostParam designation = param("designation", "ASHA");
        String panchayatCode = "S01D001B001V001";
        MyWebClient.PostParam panchayat = param("panchayat", panchayatCode);
        MyWebClient.PostParam callerId = param("session.connection.remote.uri", "555");
        new MyWebClient().post("http://localhost:9979/ananya/flw/register/", designation,panchayat ,callerId);

        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("555");
        Location location = allLocations.findByExternalId(panchayatCode);

        assertEquals(location.getId(), frontLineWorker.getLocationId());
    }

    private void assertNonInterActivePrompts() throws XPathExpressionException {
        NodeList list = callFlow.readNode("/vxml/form/block/audio");
        assertEquals(1, list.getLength());
    }

    private void assertOnRecord(String param) throws XPathExpressionException {
        String record = "/vxml/form/record[@name='" + param + "']";
        assertNotNull(callFlow.readNode(record + "/noinput/prompt"));
        assertNotNull(callFlow.readNode(record + "/prompt"));
        assertNotNull(callFlow.readNode(record + "/grammer"));
        assertNotNull(callFlow.readNode(record + "/filled/prompt"));
    }

    private void assertOnRecordConfirm(String param) throws XPathExpressionException {
        String confirmParam = param + "Confirm";
        String paramNotRecorded = param + "NotRecorded";
        String field = "/vxml/form/field[@name='" + confirmParam + "']";

        assertEquals("true", callFlow.readString("/vxml/var[@name='" + paramNotRecorded + "']/@expr"));
        assertEquals(paramNotRecorded, callFlow.readString(field + "/@cond"));

        assertNotNull(callFlow.readNode(field + "/prompt"));
        assertNotNull(callFlow.readNode(field + "/grammer"));
        assertNotNull(callFlow.readNode(field + "/filled"));
        assertNotNull(callFlow.readNode(field + "/prompt/audio[@expr=" + param + "]"));

        assertEquals(confirmParam, callFlow.readString(field + "/filled/if/@cond"));
        assertEquals("#register_flw", callFlow.readString(field + "/filled/if/goto/@next"));
        assertEquals("false", callFlow.readString(field + "/filled/if/assign[@name = '" + paramNotRecorded + "']/@expr"));
    }

    private void assertOnDesignationField() throws XPathExpressionException {
        String designation = "/vxml/form/field[@name='designation']";
        assertNotNull(callFlow.readNode(designation));
        assertNotNull(callFlow.readNode(designation + "/prompt"));

        assertEquals("true", callFlow.readString("/vxml/var[@name='designationNotRecorded']/@expr"));
        assertEquals(Designation.ANM.name(), callFlow.readString(designation + "/option[@dtmf=1]/@value"));
        assertEquals(Designation.ASHA.name(), callFlow.readString(designation + "/option[@dtmf=2]/@value"));
        assertEquals(Designation.ANGANWADI.name(), callFlow.readString(designation + "/option[@dtmf=3]/@value"));
        assertEquals("false",callFlow.readString(designation + "/filled/assign[@name='designationNotRecorded']/@expr"));
    }
}
