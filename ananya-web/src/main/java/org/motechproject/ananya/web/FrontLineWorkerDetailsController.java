package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.ValidationResponse;
import org.motechproject.ananya.domain.WebRequestValidator;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.FrontLineWorkerUsageResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.FLWDetailsService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.web.annotations.Authenticated;
import org.motechproject.ananya.web.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Authenticated
@Controller
@RequestMapping(value = "/flw")
public class FrontLineWorkerDetailsController extends BaseDataAPIController {
    private RegistrationService registrationService;
    private FLWDetailsService flwDetailsService;

    @Autowired
    public FrontLineWorkerDetailsController(RegistrationService registrationService, FLWDetailsService flwDetailsService) {
        this.registrationService = registrationService;
        this.flwDetailsService = flwDetailsService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    RegistrationResponse create(@RequestBody FrontLineWorkerRequest request) {
        return registrationService.createOrUpdateFLW(request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public
    @ResponseBody
    List<FrontLineWorkerResponse> search(HttpServletRequest request) {
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
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{flwGuid}/usage")
    public
    @ResponseBody
    FrontLineWorkerUsageResponse getFLWUsageDetails(@PathVariable String flwGuid, @RequestParam String channel){
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        ValidationResponse validationResponse = webRequestValidator.validateChannel(channel);
        raiseExceptionIfThereAreErrors(validationResponse);

        return flwDetailsService.getUsageData(flwGuid);
    }

    private void raiseExceptionIfThereAreErrors(ValidationResponse validationResponse) {
        if (validationResponse.hasErrors()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
    }
}
