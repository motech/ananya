package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.response.FrontLineWorkerUsageResponse;
import org.motechproject.ananya.service.FLWDetailsService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.web.exception.ValidationException;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerDetailsControllerTest {

    private FrontLineWorkerDetailsController frontLineWorkerDetailsController;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private FLWDetailsService flwDetailsService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        frontLineWorkerDetailsController = new FrontLineWorkerDetailsController(registrationService, flwDetailsService);
    }

    @Test
    public void shouldGetFLWUsageDetails() {
        String flwId = UUID.randomUUID().toString();
        FrontLineWorkerUsageResponse expectedFLWUsageResponse = new FrontLineWorkerUsageResponse();
        when(flwDetailsService.getUsageData(flwId)).thenReturn(expectedFLWUsageResponse);

        FrontLineWorkerUsageResponse actualFLWUsageResponse = frontLineWorkerDetailsController.getFLWUsageDetails(flwId, "contact_center");

        assertEquals(expectedFLWUsageResponse, actualFLWUsageResponse);
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValid() {
        String channel = "invalid_channel";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Invalid channel: %s", channel));
        frontLineWorkerDetailsController.getFLWUsageDetails("flwId", channel);
    }
}
