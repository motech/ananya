package org.motechproject.ananya.web.request;

import org.junit.Test;
import org.motechproject.ananya.response.ValidationResponse;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class FLWUsageWebRequestTest {


    @Test
    public void shouldValidateRequest() {
        FLWUsageWebRequest flwUsageWebRequest = new FLWUsageWebRequest("msisdn", "channel");

        ValidationResponse validationResponse = flwUsageWebRequest.validate();

        List<String> errors = validationResponse.getErrors();
        assertTrue(errors.contains("invalid msisdn: msisdn"));
        assertTrue(errors.contains("invalid channel: channel"));
    }
}
