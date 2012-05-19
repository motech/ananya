package org.motechproject.ananya.web;

import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/data/flw")
public class FrontLineWorkerDetailsController {

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(method = RequestMethod.POST)
    public void create(@RequestBody FrontLineWorkerRequest request) {
        registrationService.createOrUpdateFLW(request);
    }
}
