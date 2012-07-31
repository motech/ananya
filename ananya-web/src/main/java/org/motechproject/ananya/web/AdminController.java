package org.motechproject.ananya.web;


import org.motechproject.ananya.domain.MonitorPage;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private DiagnosticService diagnosticService;
    private MonitorPage monitorPage;

    @Autowired
    public AdminController(DiagnosticService diagnosticService, MonitorPage monitorPage) {
        this.diagnosticService = diagnosticService;
        this.monitorPage = monitorPage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/diagnostics")
    @ResponseBody
    public String getDiagnostics() throws Exception {
        String diagnosisResult = diagnosticService.getDiagnostics();
        log.info("diagnostics called");
        return diagnosisResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/monitor")
    public ModelAndView showMonitorPage() throws Exception {
        log.info("monitor page displayed");
        return monitorPage.display();
    }

}