package org.motechproject.ananya.web;

import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.web.annotations.Authenticated;
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
public class LocationDetailsController {

    @Autowired
    private LocationRegistrationService locationRegistrationService;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody LocationRegistrationResponse create(@RequestBody LocationRequest request) {
        return locationRegistrationService.addNewLocation(request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public @ResponseBody List<LocationResponse> search(HttpServletRequest request) {
        String district = request.getParameter("district");
        String block = request.getParameter("block");
        String panchayat = request.getParameter("panchayat");
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);

        return locationRegistrationService.getFilteredLocations(locationRequest);
    }
}
