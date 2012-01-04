package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.bbcwt.repository.AllRecordings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private static final String registration_vxml = "register-flw";
    private static final String registration_done_vxml = "register-done-flw";
    private static final String caller_landing_vxml = "callerLandingPage";
    private static final String menu_vxml = "top-menu";
    private static final String msisdn_param = "msisdn";
    private static final String callerid_param = "session.callerid";

    private FrontLineWorkerService frontLineWorkerService;
    private AllRecordings allRecordings;

    @Autowired
    public RegistrationController(FrontLineWorkerService frontLineWorkerService, AllRecordings allRecordings) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRecordings = allRecordings;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/landing/")
    public ModelAndView getLandingPage(HttpServletRequest request) {
        String msisdn = request.getParameter(callerid_param);
        log.info("msisdn of caller: " + msisdn);
        String vxml = frontLineWorkerService.getStatus(msisdn).isRegistered() ? "/vxml/menu/": "/vxml/register/";
        return new ModelAndView(caller_landing_vxml).addObject("rendering_Page", vxml);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/register/")
    public ModelAndView getRegisterPage(HttpServletResponse response){
        response.setHeader("Expires", "Tue, 20 Mar 2012 04:00:25 GMT");
        response.setHeader("Cache-Control","max-age=60, public");
        return new ModelAndView(registration_vxml);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/menu/")
    public ModelAndView getMenuPage(HttpServletResponse response){
        return new ModelAndView(menu_vxml);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        List items = upload.parseRequest(request);

        String msisdn = getMsisdn(items);
        frontLineWorkerService.createNew(msisdn);
        String path = request.getSession().getServletContext().getRealPath("/recordings/");
        allRecordings.store(msisdn, items, path);

        return new ModelAndView(registration_done_vxml);
    }

    private String getMsisdn(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(msisdn_param))
                return item.getString();
        return null;
    }

}
