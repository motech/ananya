package org.motechproject.ananya.functional;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.JsonHttpClient;
import org.motechproject.ananya.web.FLWDetailsController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ExceptionScenarioTests extends SpringIntegrationTest {
    @Autowired
    private FLWDetailsController frontLineWorkerDetailsController;
    
    @Test
    public void shouldReturnStackTraceAnd500ErrorCodeInCaseOfExceptionFromDataApi() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(getAppServerHostUrl() + "/ananya/flw", String.class);
        jsonHttpClient.addHeader("APIKey", "1234");
        String response = (String) jsonHttpClient.post("\"foo\":\"bar\"");

        assertTrue(StringUtils.containsIgnoreCase(response, "Exception"));
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, jsonHttpClient.getStatus());
    }

    @Test
    public void shouldAJavascriptVarAndHttp200CodeInCaseOfExceptionFromAnanya() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(getAppServerHostUrl() + "/ananya/transferdata/disconnect", String.class);
        String response = (String) jsonHttpClient.post("");

        assertTrue(StringUtils.containsIgnoreCase(response, "ANANYA_ERROR"));
        assertEquals(HttpServletResponse.SC_OK, jsonHttpClient.getStatus());
    }
}
