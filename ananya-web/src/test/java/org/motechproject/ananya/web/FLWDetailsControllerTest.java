package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.Channel;
import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.request.FLWNighttimeCallsRequest;
import org.motechproject.ananya.response.FLWNighttimeCallsResponse;
import org.motechproject.ananya.response.FLWUsageResponse;
import org.motechproject.ananya.service.FLWDetailsService;
import org.motechproject.ananya.service.FLWRegistrationService;
import org.motechproject.ananya.utils.DateUtils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FLWDetailsControllerTest {

    private FLWDetailsController frontLineWorkerDetailsController;
    @Mock
    private FLWRegistrationService flwRegistrationService;
    @Mock
    private FLWDetailsService flwDetailsService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        frontLineWorkerDetailsController = new FLWDetailsController(flwRegistrationService, flwDetailsService);
    }

    @Test
    public void shouldGetFLWUsageDetails() {
        String msisdn = "1234567980";
        FLWUsageResponse expectedFLWUsageResponse = new FLWUsageResponse();
        when(flwDetailsService.getUsage("91" + msisdn)).thenReturn(expectedFLWUsageResponse);

        FLWUsageResponse actualFLWUsageResponse = frontLineWorkerDetailsController.getUsage(msisdn, "contact_center");

        assertEquals(expectedFLWUsageResponse, actualFLWUsageResponse);
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValid() {
        String channel = "invalid_channel";
        String msisdn = "1234567890";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid channel: %s", channel));
        frontLineWorkerDetailsController.getUsage(msisdn, channel);
    }

    @Test
    public void shouldThrowExceptionIfMsisdnNotValid() {
        String channel = "contact_center";
        String msisdn = "invalid_msisdn";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid msisdn: %s", msisdn));
        frontLineWorkerDetailsController.getUsage(msisdn, channel);
    }

    @Test
    public void shouldGetFLWNighttimeCallsDetails() {
        String msisdn = "1234567890";
        String startDate = "14-12-2009";
        String endDate = "15-12-2009";

        FLWNighttimeCallsResponse expectedNighttimeCallsResponse = mock(FLWNighttimeCallsResponse.class);
        when(flwDetailsService.getNighttimeCalls(any(FLWNighttimeCallsRequest.class))).thenReturn(expectedNighttimeCallsResponse);

        FLWNighttimeCallsResponse actualNighttimeCallsResponse = frontLineWorkerDetailsController.getNighttimeCalls(msisdn, "contact_center", startDate, endDate);

        assertEquals(expectedNighttimeCallsResponse, actualNighttimeCallsResponse);

        ArgumentCaptor<FLWNighttimeCallsRequest> captor = ArgumentCaptor.forClass(FLWNighttimeCallsRequest.class);
        verify(flwDetailsService).getNighttimeCalls(captor.capture());
        FLWNighttimeCallsRequest actualRequest = captor.getValue();
        assertEquals("91" + msisdn, actualRequest.getMsisdn());
        assertEquals(Channel.CONTACT_CENTER, actualRequest.getChannel());
        assertEquals(DateUtils.parseLocalDate(startDate), actualRequest.getStartDate());
        assertEquals(DateUtils.parseLocalDate(endDate), actualRequest.getEndDate());
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValidWhileFetchingNighttimeCalls() {
        String channel = "invalid_channel";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid channel: %s", channel));
        frontLineWorkerDetailsController.getNighttimeCalls("msisdn", channel, "", "");
    }

    @Test
    public void shouldThrowExceptionIfMsisdnIsNotValidWhileFetchingNighttimeCalls() {
        String channel = "contact_center";
        String msisdn = "invalid_msisdn";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid msisdn: %s", msisdn));
        frontLineWorkerDetailsController.getNighttimeCalls(msisdn, channel, "", "");
    }
}
