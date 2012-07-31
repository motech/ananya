package org.motechproject.ananya.domain;

import org.motechproject.ananya.support.diagnostics.CouchDBDiagnostic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Service
public class MonitorPage {

    private String view = "admin/monitor";

    private CouchDBDiagnostic couchDBDiagnostic;

    @Autowired
    public MonitorPage(CouchDBDiagnostic couchDBDiagnostic) {
        this.couchDBDiagnostic = couchDBDiagnostic;
    }

    public ModelAndView display() throws IOException {
        Map<String, String> couchDbData = couchDBDiagnostic.getResult();

        return new ModelAndView(view)
                .addObject("menuMap", new Sidebar().getMenu())
                .addObject("couchdbData", couchDbData);
    }


}
