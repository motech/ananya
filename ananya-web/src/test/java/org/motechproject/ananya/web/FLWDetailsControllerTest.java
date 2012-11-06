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
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.utils.DateUtils;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FLWDetailsControllerTest {

    private FLWDetailsController frontLineWorkerDetailsController;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private FLWDetailsService flwDetailsService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        frontLineWorkerDetailsController = new FLWDetailsController(registrationService, flwDetailsService);
    }

    @Test
    public void shouldGetFLWUsageDetails() {
        String flwId = UUID.randomUUID().toString();
        FLWUsageResponse expectedFLWUsageResponse = new FLWUsageResponse();
        when(flwDetailsService.getUsage(flwId)).thenReturn(expectedFLWUsageResponse);

        FLWUsageResponse actualFLWUsageResponse = frontLineWorkerDetailsController.getUsage(flwId, "contact_center");

        assertEquals(expectedFLWUsageResponse, actualFLWUsageResponse);
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValid() {
        String channel = "invalid_channel";
        String flwId = UUID.randomUUID().toString();

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid channel: %s", channel));
        frontLineWorkerDetailsController.getUsage(flwId, channel);
    }

    @Test
    public void shouldThrowExceptionIfFLWIdIsNotValid() {
        String channel = "contact_center";
        String flwId = "invalid_flwId";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid flw id: %s", flwId));
        frontLineWorkerDetailsController.getUsage(flwId, channel);
    }

    @Test
    public void shouldGetFLWNighttimeCallsDetails() {
        UUID flwId = UUID.randomUUID();
        String startDate = "14-12-2009";
        String endDate = "15-12-2009";

        FLWNighttimeCallsResponse expectedNighttimeCallsResponse = mock(FLWNighttimeCallsResponse.class);
        when(flwDetailsService.getNighttimeCalls(any(FLWNighttimeCallsRequest.class))).thenReturn(expectedNighttimeCallsResponse);

        FLWNighttimeCallsResponse actualNighttimeCallsResponse = frontLineWorkerDetailsController.getNighttimeCalls(flwId.toString(), "contact_center", startDate, endDate);

        assertEquals(expectedNighttimeCallsResponse, actualNighttimeCallsResponse);

        ArgumentCaptor<FLWNighttimeCallsRequest> captor = ArgumentCaptor.forClass(FLWNighttimeCallsRequest.class);
        verify(flwDetailsService).getNighttimeCalls(captor.capture());
        FLWNighttimeCallsRequest actualRequest = captor.getValue();
        assertEquals(flwId, actualRequest.getFlwId());
        assertEquals(Channel.CONTACT_CENTER, actualRequest.getChannel());
        assertEquals(DateUtils.parseLocalDate(startDate), actualRequest.getStartDate());
        assertEquals(DateUtils.parseLocalDate(endDate), actualRequest.getEndDate());
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValidWhileFetchingNighttimeCalls() {
        String channel = "invalid_channel";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid channel: %s", channel));
        frontLineWorkerDetailsController.getNighttimeCalls("flwId", channel, "", "");
    }

    @Test
    public void shouldThrowExceptionIfFLWIdIsNotValidWhileFetchingNighttimeCalls() {
        String channel = "contact_center";
        String flwId = "invalid_flwId";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("invalid flw id: %s", flwId));
        frontLineWorkerDetailsController.getNighttimeCalls(flwId, channel, "", "");
    }
}
