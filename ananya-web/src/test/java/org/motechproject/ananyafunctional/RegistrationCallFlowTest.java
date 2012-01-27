package org.motechproject.ananyafunctional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class RegistrationCallFlowTest extends SpringIntegrationTest {

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
        assertEquals(4, list.getLength());
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

        assertEquals("dtmf", callFlow.readString(designation + "/grammar/@mode"));
        NodeList options = callFlow.readNode(designation + "/grammar/rule/one-of/item");
        assertEquals("1", options.item(0).getTextContent());
        assertEquals("2", options.item(1).getTextContent());
        assertEquals("3", options.item(2).getTextContent());
    }

}
