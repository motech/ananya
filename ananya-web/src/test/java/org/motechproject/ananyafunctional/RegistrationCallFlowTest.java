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

        for (String record : asList("name", "district", "block", "village"))
            assertOnRecordElement(callFlow, record);

        NodeList read = (NodeList) callFlow.read("/vxml/form/var[@name='msisdn']", XPathConstants.NODESET);
        assertEquals("session.callerid", read.item(0).getAttributes().item(0).getTextContent());
        assertEquals("/vxml/jobaid.vxml", callFlow.read("/vxml/form/block/goto/@next", XPathConstants.STRING));
    }

    private void assertOnRecordElement(CallFlow callFlow, String param) throws XPathExpressionException {
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/noinput/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/grammer", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/filled/prompt", XPathConstants.NODESET));
    }
}
