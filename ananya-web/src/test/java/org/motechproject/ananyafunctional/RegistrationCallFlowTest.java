package org.motechproject.ananyafunctional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class RegistrationCallFlowTest {

    private CallFlow callFlow;

    @Before
    public void setUp() throws Exception {
        MyWebClient myWebClient = new MyWebClient();
        callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/jobaid/register/");
    }

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws Exception {
        for (String record : asList("name", "district", "block", "panchayat")) {
            assertOnRecord(record);
            assertOnRecordConfirm(record);
        }
        assertOnDesignationField();
        assertNonInterActivePrompts();
        assertEquals("/ananya/vxml/jobaid.vxml", callFlow.readString("/vxml/form/block/goto/@next"));
        assertEquals("session.connection.local.uri designation name district block panchayat", callFlow.readString("/vxml/form/block/data/@namelist"));
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

        assertEquals("true", callFlow.readString("/vxml/form/var[@name='" + paramNotRecorded + "']/@expr"));
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

        assertEquals(Designation.ANM.name(), callFlow.readString(designation + "/option[@dtmf=1]/@value"));
        assertEquals(Designation.ASHA.name(), callFlow.readString(designation + "/option[@dtmf=2]/@value"));
        assertEquals(Designation.ANGANWADI.name(), callFlow.readString(designation + "/option[@dtmf=3]/@value"));
    }

}
