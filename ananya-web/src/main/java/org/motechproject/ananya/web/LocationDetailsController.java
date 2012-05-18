package org.motechproject.ananya.web;

import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/data/location")
public class LocationDetailsController {

    @Autowired
    private LocationRegistrationService locationRegistrationService;

    @RequestMapping(method = RequestMethod.POST)
    public void create(HttpServletRequest request) {
        String district = request.getParameter("district");
        String block = request.getParameter("block");
        String panchayat = request.getParameter("panchayat");

        locationRegistrationService.addNewLocation(district, block, panchayat);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getLocation(HttpServletRequest request) {
        String district = request.getParameter("district");
        String block = request.getParameter("block");
        String panchayat = request.getParameter("panchayat");

        List<LocationResponse> filteredLocations = locationRegistrationService.getFilteredLocations(district, block, panchayat);

        return new ModelAndView("locations").addObject("filteredLocations", filteredLocations);
    }
}
