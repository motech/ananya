package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdminControllerTest {

    @Mock
    private DiagnosticService diagnosticService;
    private AdminController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new AdminController(diagnosticService);
    }

    @Test
    public void shouldCallDiagnosticService() throws Exception {
        String diagnosis = "diagnosis";
        when(diagnosticService.getDiagnostics()).thenReturn(diagnosis);

        String results = controller.getDiagnostics();
        assertThat(results, is(diagnosis));
    }
}
