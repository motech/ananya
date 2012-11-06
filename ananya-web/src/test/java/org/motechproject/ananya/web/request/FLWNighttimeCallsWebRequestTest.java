package org.motechproject.ananya.web.request;

import org.junit.Test;
import org.motechproject.ananya.domain.Channel;
import org.motechproject.ananya.request.FLWNighttimeCallsRequest;
import org.motechproject.ananya.response.ValidationResponse;
import org.motechproject.ananya.utils.DateUtils;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FLWNighttimeCallsWebRequestTest {

    @Test
    public void shouldValidate() {
        FLWNighttimeCallsWebRequest webRequest = new FLWNighttimeCallsWebRequest("invalid_flw_id", "invalid_channel", "invalid_start_date", "invalid_end_date");
        ValidationResponse validationResponse = webRequest.validate();
        assertEquals("invalid flw id: invalid_flw_id,invalid channel: invalid_channel,invalid start date: invalid_start_date,invalid end date: invalid_end_date", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldReturnDomainRequest() {
        UUID flwId = UUID.randomUUID();
        FLWNighttimeCallsWebRequest webRequest = new FLWNighttimeCallsWebRequest(flwId.toString(), "contact_center", "14-12-2009", "15-12-2009");
        FLWNighttimeCallsRequest request = webRequest.getRequest();

        assertEquals(flwId, request.getFlwId());
        assertEquals(Channel.CONTACT_CENTER, request.getChannel());
        assertEquals(DateUtils.parseLocalDate("14-12-2009"), request.getStartDate());
        assertEquals(DateUtils.parseLocalDate("15-12-2009"), request.getEndDate());
    }
}
