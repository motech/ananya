package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.PhoneNumber;
import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.*;
import org.motechproject.ananya.service.FLWDetailsService;
import org.motechproject.ananya.service.FLWRegistrationService;
import org.motechproject.ananya.web.annotations.Authenticated;
import org.motechproject.ananya.web.request.FLWNighttimeCallsWebRequest;
import org.motechproject.ananya.web.request.FLWUsageWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Authenticated
@Controller
@RequestMapping(value = "/flw")
public class FLWDetailsController extends BaseDataAPIController {
    private FLWRegistrationService flwRegistrationService;
    private FLWDetailsService flwDetailsService;

    @Autowired
    public FLWDetailsController(FLWRegistrationService flwRegistrationService, FLWDetailsService flwDetailsService) {
        this.flwRegistrationService = flwRegistrationService;
        this.flwDetailsService = flwDetailsService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    RegistrationResponse createOrUpdate(@RequestBody FrontLineWorkerRequest request) {
        return flwRegistrationService.createOrUpdateFLW(request);
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

        return flwRegistrationService.getFilteredFLW(msisdnAsLong, name, status, designation, operator, circle, startDate, endDate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{msisdn}/usage", produces = {"application/json", "application/xml"})
    @ResponseBody
    public FLWUsageResponse getUsage(@PathVariable String msisdn, @RequestParam String channel){
        FLWUsageWebRequest flwUsageWebRequest = new FLWUsageWebRequest(msisdn, channel);
        ValidationResponse validationResponse = flwUsageWebRequest.validate();
        raiseExceptionIfThereAreErrors(validationResponse);
        String formattedMsisdn = new PhoneNumber(msisdn).getFormattedMsisdn();

        return flwDetailsService.getUsage(formattedMsisdn);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{msisdn}/nighttimecalls", produces = {"application/json", "application/xml"})
    @ResponseBody
    public FLWNighttimeCallsResponse getNighttimeCalls(@PathVariable String msisdn, @RequestParam String channel, @RequestParam String startDate, @RequestParam String endDate){
        FLWNighttimeCallsWebRequest flwNighttimeCallsRequest = new FLWNighttimeCallsWebRequest(msisdn, channel, startDate, endDate);
        ValidationResponse validationResponse = flwNighttimeCallsRequest.validate();
        raiseExceptionIfThereAreErrors(validationResponse);

        return flwDetailsService.getNighttimeCalls(flwNighttimeCallsRequest.getRequest());
    }

    private void raiseExceptionIfThereAreErrors(ValidationResponse validationResponse) {
        if (validationResponse.hasErrors()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
    }
}
