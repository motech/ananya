package org.motechproject.ananya.web;

import org.motechproject.ananya.service.LocationRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping (value = "/data/location")
public class LocationDetailsController {

    @Autowired
    private LocationRegistrationService locationRegistrationService;

    @RequestMapping (method = RequestMethod.POST, value = "/add")
    public void create(@RequestParam String district, @RequestParam String block, @RequestParam String panchayat) {
        locationRegistrationService.registerLocation(district, block, panchayat);
    }
}
