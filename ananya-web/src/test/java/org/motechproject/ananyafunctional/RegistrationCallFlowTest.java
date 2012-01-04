package org.motechproject.ananyafunctional;

import org.junit.Test;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class RegistrationCallFlowTest extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws Exception {

        MyWebClient myWebClient = new MyWebClient();
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/flw/vxml/?session.callerid=123");

        for (String record : asList("name", "district", "block", "village"))
            assertOnRecordElement(callFlow, record);

        NodeList read = (NodeList) callFlow.read("/vxml/form/var[@name='msisdn']", XPathConstants.NODESET);
        assertEquals("session.callerid", read.item(0).getAttributes().item(0).getTextContent());
    }

    private void assertOnRecordElement(CallFlow callFlow, String param) throws XPathExpressionException {
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/noinput/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/prompt", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/grammer", XPathConstants.NODESET));
        assertNotNull(callFlow.read("/vxml/form/record[@name='" + param + "']/filled/prompt", XPathConstants.NODESET));
    }


}
