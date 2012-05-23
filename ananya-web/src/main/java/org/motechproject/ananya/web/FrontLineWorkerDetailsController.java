package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.web.annotations.Authenticated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Authenticated
@Controller
@RequestMapping(value = "/data/flw")
public class FrontLineWorkerDetailsController {

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@RequestBody FrontLineWorkerRequest request) {
        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(request);
        return new ModelAndView("creation").addObject("response", registrationResponse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public ModelAndView search(HttpServletRequest request) {
        String msisdn = request.getParameter("msisdn");
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String designation = request.getParameter("designation");
        String operator = request.getParameter("operator");
        String circle = request.getParameter("circle");
        String activityStartDate = request.getParameter("activityStartDate");
        String activityEndDate = request.getParameter("activityEndDate");
        Long msisdnAsLong = msisdn != null ? Long.parseLong(msisdn) : null;
        Date startDate = activityStartDate !=null ? DateTime.parse(activityStartDate).toDate() : null;
        Date endDate = activityEndDate !=null ? DateTime.parse(activityEndDate).toDate() : null;

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdnAsLong, name, status, designation, operator, circle, startDate, endDate);

        return new ModelAndView("get_filtered_results").addObject("filteredResponse", filteredFLW);
    }
}
