package org.motechproject.ananyafunctional;

import org.junit.Test;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class RegistrationCallFlowTest extends SpringIntegrationTest {

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws Exception {

        MyWebClient myWebClient = new MyWebClient();
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/jobaid/register/");

        NodeList nonInteractivePrompts = (NodeList) callFlow.read("/vxml/form/block/audio", XPathConstants.NODESET);
        assertEquals(4, nonInteractivePrompts.getLength());

        assertOnDesignationField(callFlow);
        
        for (String record : asList("name", "district", "block", "panchayat")) {
            assertOnRecord(callFlow, record);
            assertOnRecordConfirm(callFlow, record);
        }

        assertEquals("/ananya/vxml/jobaid.vxml", callFlow.read("/vxml/form/block/goto/@next", XPathConstants.STRING));
    }

    private void assertOnRecord(CallFlow callFlow, String param) throws XPathExpressionException {
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/noinput/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/grammer", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/filled/prompt", XPathConstants.NODESET));
    }

    private void assertOnRecordConfirm(CallFlow callFlow, String param) throws XPathExpressionException {
        String confirmParam = param + "Confirm";
        String paramNotRecorded = param + "NotRecorded";

        assertEquals("true", callFlow.read("/vxml/form/var[@name='" + paramNotRecorded + "']/@expr", XPathConstants.STRING));

        assertNotNull(callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/grammer", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/filled", XPathConstants.NODESET));

        assertEquals(paramNotRecorded, callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/@cond", XPathConstants.STRING));
        assertNotNull(callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/prompt/audio[@expr=" + param + "]", XPathConstants.NODESET));

        assertEquals(confirmParam, callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/filled/if/@cond", XPathConstants.STRING));
        assertEquals("#register_flw", callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/filled/if/goto/@next", XPathConstants.STRING));
        assertEquals("false", callFlow.read("/vxml/form/field[@name='" + confirmParam + "']/filled/if/assign[@name = '" + paramNotRecorded + "']/@expr", XPathConstants.STRING));
    }

    private void assertOnDesignationField(CallFlow callFlow) throws XPathExpressionException {
        String designationField = "designation";
        assertNotNull(callFlow.read("/vxml/form/field[@name='" + designationField + "']", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/field[@name='" + designationField + "']/prompt", XPathConstants.NODESET));
        assertEquals("dtmf", callFlow.read("/vxml/form/field[@name='" + designationField + "']/grammar/@mode", XPathConstants.STRING));
        NodeList dtmfOptions = (NodeList) callFlow.read("/vxml/form/field[@name='" + designationField + "']/grammar/rule/one-of/item", XPathConstants.NODESET);

        assertEquals("1",  dtmfOptions.item(0).getTextContent());
        assertEquals("2",  dtmfOptions.item(1).getTextContent());
        assertEquals("3",  dtmfOptions.item(2).getTextContent());
    }

}
