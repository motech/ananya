package org.motechproject.ananya.web;


import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController{

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private DiagnosticService diagnosticService;

    @Autowired
    public AdminController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/diagnostics")
    @ResponseBody
    public String getDiagnostics() throws Exception {
        String diagnosisResult = diagnosticService.getDiagnostics();
        log.error("9986574410-1234567"+"|"+"NullPointer");
        return diagnosisResult;
    }

}