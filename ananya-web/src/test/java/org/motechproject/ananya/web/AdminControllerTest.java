package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.LoginPage;
import org.motechproject.ananya.domain.MonitorPage;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdminControllerTest {

    private AdminController controller;
    @Mock
    private DiagnosticService diagnosticService;
    @Mock
    private MonitorPage monitorPage;
    @Mock
    private LoginPage loginPage;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new AdminController(diagnosticService, monitorPage, loginPage);
    }

    @Test
    public void shouldCallDiagnosticService() throws Exception {
        String diagnosis = "diagnosis";
        when(diagnosticService.getDiagnostics()).thenReturn(diagnosis);

        String results = controller.getDiagnostics();
        assertThat(results, is(diagnosis));
    }

    @Test
    public void shouldCallMonitorPageToDisplayResults() throws Exception {
        ModelAndView modelAndView = mock(ModelAndView.class);
        when(monitorPage.display()).thenReturn(modelAndView);

        ModelAndView actualView = controller.showMonitorPage();
        assertEquals(modelAndView, actualView);

        verify(monitorPage).display();
    }
}
