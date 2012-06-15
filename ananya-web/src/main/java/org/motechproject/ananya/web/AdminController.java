package org.motechproject.ananya.web;


import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.support.diagnostics.base.DiagnosticService;
import org.motechproject.ananya.views.FrontLineWorkerPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class AdminController{
    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private DiagnosticService diagnosticService;

    @Autowired
    @Qualifier("ananyaProperties")
    private Properties properties;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/admin/")
    public ModelAndView showTestPage(HttpServletRequest request) {
        log.info("Fetching workers" );

        List<FrontLineWorkerPresenter> workerPresenters = new ArrayList<FrontLineWorkerPresenter>();
        List<FrontLineWorker> workers = allFrontLineWorkers.getAll();
        try {
            for (FrontLineWorker worker : workers) {
                Location location = allLocations.findByExternalId(worker.getLocationId());
                workerPresenters.add(new FrontLineWorkerPresenter(worker.getId(), worker.getMsisdn(), worker.getStatus().toString(),
                        location.getBlock(), location.getDistrict(), location.getPanchayat(), worker.getRegisteredDate()));
            }
        } catch (Exception e) {
            log.error("Exception:", e);
        }

        return new ModelAndView("admin")
                .addObject("workerPresenters", workerPresenters)
                .addObject("contextPathWithVersion", contextWithVersion(request));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/flw/delete")
    @ResponseBody
    public String removeFlw(String id) {
        log.info("Removing FLW with id " + id );

        FrontLineWorker flwToDelete = allFrontLineWorkers.get(id);

        if (flwToDelete != null) {
            allFrontLineWorkers.remove(flwToDelete);
            log.info("Deleted : " + flwToDelete);
        }
        return "Deleted : " + flwToDelete;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/diagnostics")
    @ResponseBody
    public String getDiagnostics() throws Exception {
        String diagnosisResult = diagnosticService.getDiagnostics();
        return diagnosisResult;
    }

    private String contextWithVersion(HttpServletRequest request) {
        return request.getContextPath() + "/" + properties.getProperty("url.version");
    }
}