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
        String flwGuid = "flwGuid";
        FrontLineWorkerUsageResponse expectedFLWUsageResponse = new FrontLineWorkerUsageResponse();
        when(flwDetailsService.getUsageData(flwGuid)).thenReturn(expectedFLWUsageResponse);

        FrontLineWorkerUsageResponse actualFLWUsageResponse = frontLineWorkerDetailsController.getFLWUsageDetails(flwGuid, "contact_center");

        assertEquals(expectedFLWUsageResponse, actualFLWUsageResponse);
    }

    @Test
    public void shouldThrowExceptionIfChannelIsNotValid() {
        String channel = "invalid_channel";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Invalid channel: %s", channel) + System.lineSeparator());
        frontLineWorkerDetailsController.getFLWUsageDetails("flwGuid", channel);
    }
}
