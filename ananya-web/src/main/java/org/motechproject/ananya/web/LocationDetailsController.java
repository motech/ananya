package org.motechproject.ananya.web;

import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.web.annotations.Authenticated;
import org.motechproject.ananya.web.exception.ValidationException;
import org.motechproject.ananya.web.validator.Errors;
import org.motechproject.ananya.web.validator.LocationSyncRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Authenticated
@Controller
@RequestMapping(value = "/location")
public class LocationDetailsController extends BaseDataAPIController {
    private LocationRegistrationService locationRegistrationService;

    @Autowired
    public LocationDetailsController(LocationRegistrationService locationRegistrationService) {
        this.locationRegistrationService = locationRegistrationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    void create(@RequestBody LocationSyncRequest locationSyncRequest) {
        Errors errors = LocationSyncRequestValidator.validate(locationSyncRequest);
        raiseExceptionOnError(errors);
        locationRegistrationService.addNewLocation(locationSyncRequest);
    }

    private void raiseExceptionOnError(Errors errors) {
        if (errors.hasErrors())
            throw new ValidationException(errors.allMessages());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public
    @ResponseBody
    List<LocationResponse> search(HttpServletRequest request) {
        String district = request.getParameter("district");
        String block = request.getParameter("block");
        String panchayat = request.getParameter("panchayat");
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);

        return locationRegistrationService.getFilteredLocations(locationRequest);
    }
}
