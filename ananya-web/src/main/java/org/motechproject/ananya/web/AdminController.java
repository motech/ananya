package org.motechproject.ananya.web;


import org.motechproject.ananya.domain.page.InquiryPage;
import org.motechproject.ananya.domain.page.LoginPage;
import org.motechproject.ananya.domain.page.MonitorPage;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private DiagnosticService diagnosticService;
    private MonitorPage monitorPage;
    private LoginPage loginPage;
    private InquiryPage inquiryPage;

    @Autowired
    public AdminController(DiagnosticService diagnosticService, MonitorPage monitorPage, LoginPage loginPage, InquiryPage inquiryPage) {
        this.diagnosticService = diagnosticService;
        this.monitorPage = monitorPage;
        this.loginPage = loginPage;
        this.inquiryPage = inquiryPage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/diagnostics")
    @ResponseBody
    public String getDiagnostics() throws Exception {
        String diagnosisResult = diagnosticService.getDiagnostics();
        log.info("diagnostics called");
        return diagnosisResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/login")
    public ModelAndView login(HttpServletRequest request) {
        final String error = request.getParameter("login_error");
        return loginPage.display(error);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/monitor")
    public ModelAndView showMonitorPage() throws Exception {
        log.info("monitor page displayed");
        return monitorPage.display();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/inquiry")
    public ModelAndView showInquiryPage() throws Exception {
        log.info("inquiry page displayed");
        return inquiryPage.display();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/inquiry/data")
    @ResponseBody
    public Map<String, Object> showInquiryPage(@RequestParam String msisdn) throws Exception {
        log.info("inquiry data sent");
        return inquiryPage.display(msisdn);
    }
}