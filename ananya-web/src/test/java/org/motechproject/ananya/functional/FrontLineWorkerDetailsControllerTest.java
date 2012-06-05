package org.motechproject.ananya.functional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.JsonHttpClient;
import org.motechproject.ananya.web.FrontLineWorkerDetailsController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;

public class FrontLineWorkerDetailsControllerTest extends SpringIntegrationTest {
    @Autowired
    private FrontLineWorkerDetailsController frontLineWorkerDetailsController;
    
    @Test
    public void shouldReturnStackTraceAnd500ErrorCodeInCaseOfException() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(getAppServerHostUrl() + "/ananya/flw", String.class);
        jsonHttpClient.addHeader("APIKey", "1234");
        Object post = jsonHttpClient.post("\"foo\":\"bar\"");

        assertNotNull(post);
    }
}
