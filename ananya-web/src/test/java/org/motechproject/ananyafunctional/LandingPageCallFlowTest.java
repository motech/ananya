package org.motechproject.ananyafunctional;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;

import static junit.framework.Assert.assertEquals;

@Ignore
public class LandingPageCallFlowTest extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetLandingPageWithRegistrationUrlForUnregisteredFLW() throws Exception {
        MyWebClient myWebClient = new MyWebClient();
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/landing/");

        NodeList read = (NodeList) callFlow.read("/vxml/form/block/goto", XPathConstants.NODESET);
        assertEquals("/ananya/vxml/register/", read.item(0).getAttributes().item(0).getTextContent());
    }

    @Test
    public void shouldGetLandingPageWithMenuUrlForRegisteredFLW() throws Exception {
        MyWebClient myWebClient = new MyWebClient();
        FrontLineWorker flw = new FrontLineWorker("123").status(FrontLineWorkerStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/landing/?session.callerid=123");

        NodeList read = (NodeList) callFlow.read("/vxml/form/block/goto", XPathConstants.NODESET);
        assertEquals("/ananya/vxml/menu/", read.item(0).getAttributes().item(0).getTextContent());
    }
}
