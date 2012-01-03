package org.motechproject.ananyafunctional;

import org.junit.Test;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.BaseCallFlowTest;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.springframework.beans.factory.annotation.Autowired;

public class RegistrationCallFlowTest extends BaseCallFlowTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws Exception {
        MyWebClient myWebClient = new MyWebClient();
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/flw/vxml/?session.callerid=123");
        String vxml = callFlow.vxml();
        assertXpathExists("//prompt", transform(vxml));
    }
}
