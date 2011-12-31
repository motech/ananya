package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static final String registration_vxml = "register-flw";
    private static final String menu_vxml = "top-menu";
    private static final String msisdn = "msisdn";
    private static final String xml = "text/xml";

    private FrontLineWorkerService flwService;

    @Autowired
    public RegistrationController(FrontLineWorkerService flwService) {
        this.flwService = flwService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/flw/vxml/")
    public ModelAndView callFlow(HttpServletRequest request, HttpServletResponse response) {
        String msisdn = request.getParameter(RegistrationController.msisdn);
        response.setContentType(xml);
        String vxml = flwService.getStatus(msisdn).isRegistered() ? menu_vxml : registration_vxml;
        return new ModelAndView(vxml);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public String registerNew(HttpServletRequest request) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        return flwService.createNew(upload.parseRequest(request));
    }

}
