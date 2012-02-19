package org.motechproject.ananya.web;


import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.view.FrontLineWorkerPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllLocations allLocations;

    @RequestMapping(method = RequestMethod.GET, value = "/admin/")
    public ModelAndView show() {
        List<FrontLineWorkerPresenter> workerPresenters = new ArrayList<FrontLineWorkerPresenter>();
        List<FrontLineWorker> workers = allFrontLineWorkers.getAll();
        for (FrontLineWorker worker : workers) {
            Location location = allLocations.get(worker.getLocationId());
            workerPresenters.add(new FrontLineWorkerPresenter(worker.getId(), worker.getMsisdn(), worker.status().toString(),
                    location.blockName(), location.district(), location.panchayat()));
        }
        return new ModelAndView("admin").addObject("workerPresenters",workerPresenters);
    }
}