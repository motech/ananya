package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.support.diagnostics.CouchDBDiagnostic;
import org.motechproject.ananya.support.diagnostics.PostgresDiagnostic;
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
    private PostgresDiagnostic postgresDiagnostics;

    @Before
    public void setup() {
        initMocks(this);
        monitorPage = new MonitorPage(couchDBDiagnostic, postgresDiagnostics);
    }

    @Test
    public void shouldCallCouchDbDiagnosticsAndPostgresDiagnosticsAndPopulateModelAndView() throws IOException {
        HashMap<String, String> couchdbDiagnosticResult = new HashMap<String, String>();
        Map<String, Integer> postgresDiagnosticResult = new HashMap<String, Integer>();
        when(couchDBDiagnostic.getResult()).thenReturn(couchdbDiagnosticResult);
        when(postgresDiagnostics.getAllPostgresResults()).thenReturn(postgresDiagnosticResult);

        ModelAndView monitorDisplay = monitorPage.display();

        verify(couchDBDiagnostic).getResult();
        assertEquals(couchdbDiagnosticResult, monitorDisplay.getModel().get("couchdbData"));
        assertEquals(postgresDiagnosticResult, monitorDisplay.getModel().get("postgresData"));
        Map<String, List<MenuLink>> menuMap = (Map<String, List<MenuLink>>) monitorDisplay.getModel().get("menuMap");
        List<MenuLink> menu = menuMap.get("Production");
        assertEquals(2, menu.size());
        assertEquals("Monitor",menu.get(0).getDisplayString());
        assertEquals("Inquiry",menu.get(1).getDisplayString());
    }
}
