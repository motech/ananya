package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.page.MonitorPage;
import org.motechproject.ananya.support.diagnostics.CouchDBDiagnostic;
import org.motechproject.ananya.support.diagnostics.MachineDiagnostic;
import org.motechproject.ananya.support.diagnostics.PostgresDataDiagnostic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MonitorPageTest {
    @Autowired
    private MonitorPage monitorPage;
    @Mock
    private CouchDBDiagnostic couchDBDiagnostic;
    @Mock
    private PostgresDataDiagnostic postgresDiagnostic;
    @Mock
    private MachineDiagnostic machineDiagnostic;

    @Before
    public void setup() {
        initMocks(this);
        monitorPage = new MonitorPage(couchDBDiagnostic, postgresDiagnostic, machineDiagnostic);
    }

    @Test
    public void shouldCallCouchDbDiagnosticsAndPostgresDiagnosticsAndPopulateModelAndView() throws IOException {

        Map<String, String> couchdbDiagnosticResult = new HashMap<String, String>();
        Map<String, Map<String, String>> postgresDiagnosticResult = new HashMap<String, Map<String, String>>();
        Map<String, String> machineDiagnosticResult = new HashMap<String, String>();

        when(couchDBDiagnostic.collect()).thenReturn(couchdbDiagnosticResult);
        when(postgresDiagnostic.collectByState()).thenReturn(postgresDiagnosticResult);
        when(machineDiagnostic.collect()).thenReturn(machineDiagnosticResult);

        ModelAndView monitorDisplay = monitorPage.display();

        Map model = monitorDisplay.getModel();
        Map<String, List<MenuLink>> menuMap = (Map<String, List<MenuLink>>) model.get("menuMap");
        List<MenuLink> menu = menuMap.get("Production");

        verify(couchDBDiagnostic).collect();
        verify(postgresDiagnostic).collectByState();
        verify(machineDiagnostic).collect();

        assertEquals(couchdbDiagnosticResult, model.get("couchdbData"));
        assertEquals(postgresDiagnosticResult, model.get("postgresData"));
        assertEquals(machineDiagnosticResult, model.get("machineData"));
        assertEquals(5, menu.size());
        assertEquals("Monitor",menu.get(0).getDisplayString());
        assertEquals("Monitor [peer]",menu.get(1).getDisplayString());    }
}
