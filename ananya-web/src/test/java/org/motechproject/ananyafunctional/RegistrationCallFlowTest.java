package org.motechproject.ananyafunctional;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class RegistrationCallFlowTest extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetARegistrationVxmlForAUnRegisteredFLW() throws IOException {
        MyWebClient myWebClient = new MyWebClient();
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/flw/vxml/?session.callerid=123");
        assertTrue(StringUtils.isNotBlank(callFlow.vxml()));
        assertNotNull(allFrontLineWorkers);
    }
}
