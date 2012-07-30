package org.motechproject.ananya.web;


import org.motechproject.ananya.response.AnanyaMonitorResponse;
import org.motechproject.ananya.response.LinkMenuView;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("diagnostics called");
        return diagnosisResult;

    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/monitor")
    public ModelAndView getAnanyaMonitorInfo() {
        AnanyaMonitorResponse ananyaMonitorResponse =
                new AnanyaMonitorResponse(100, 100, 100, 100, 100, 100, 100, 100, 100);

        Map<String, List<LinkMenuView>> menuMap = new HashMap<String, List<LinkMenuView>>();
        List<LinkMenuView> linkMenuViews = new ArrayList<LinkMenuView>();
        linkMenuViews.add(new LinkMenuView("Monitor", "/admin/monitor", 0));
        linkMenuViews.add(new LinkMenuView("Monitor", "/admin/trace", 0));
        menuMap.put("Production", linkMenuViews);

        return new ModelAndView("admin/monitor")
                .addObject("ananyaMonitorResponse", ananyaMonitorResponse)
                .addObject("menuMap", menuMap);
    }

}