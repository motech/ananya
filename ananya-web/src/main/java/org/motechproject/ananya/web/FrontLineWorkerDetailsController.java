package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.exceptions.DataAPIException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Authenticated
@Controller
@RequestMapping(value = "/flw")
public class FrontLineWorkerDetailsController {
    private RegistrationService registrationService;

    @Autowired
    public FrontLineWorkerDetailsController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    RegistrationResponse create(@RequestBody FrontLineWorkerRequest request) {
        try {
            return registrationService.createOrUpdateFLW(request);
        } catch (Exception e) {
            throw new DataAPIException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public
    @ResponseBody
    List<FrontLineWorkerResponse> search(HttpServletRequest request) {
        try{
            String msisdn = request.getParameter("msisdn");
            String name = request.getParameter("name");
            String status = request.getParameter("status");
            String designation = request.getParameter("designation");
            String operator = request.getParameter("operator");
            String circle = request.getParameter("circle");
            String activityStartDate = request.getParameter("activityStartDate");
            String activityEndDate = request.getParameter("activityEndDate");
            Long msisdnAsLong = msisdn != null ? Long.parseLong(msisdn) : null;
            Date startDate = activityStartDate != null ? DateTime.parse(activityStartDate).toDate() : null;
            Date endDate = activityEndDate != null ? DateTime.parse(activityEndDate).toDate() : null;

            return registrationService.getFilteredFLW(msisdnAsLong, name, status, designation, operator, circle, startDate, endDate);
        } catch (Exception e) {
            throw new DataAPIException(e);
        }
    }
}
