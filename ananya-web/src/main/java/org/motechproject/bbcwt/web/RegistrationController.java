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
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private static final String REGISTRATION_VXML = "register-flw";
    private static final String REGISTRATION_DONE_VXML = "register-done-flw";
    private static final String LANDING_VXML = "caller-landing-page";
    private static final String MENU_VXML = "top-menu";

    private static final String MSISDN_PARAM = "msisdn";
    private static final String CALLERID_PARAM = "session.callerid";
    private static final String SESSION_CALLERID_PARAM = "session.connection.remote.uri";

    private FrontLineWorkerService frontLineWorkerService;
    private AllRecordings allRecordings;

    @Autowired
    public RegistrationController(FrontLineWorkerService frontLineWorkerService, AllRecordings allRecordings) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRecordings = allRecordings;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/landing/")
    public ModelAndView getLandingPage(HttpServletRequest request) {
        String msisdn = getCallerId(request);
        log.info("msisdn of caller: " + msisdn);
        String vxml = frontLineWorkerService.getStatus(msisdn).isRegistered() ? "/vxml/menu/" : "/vxml/register/";
        return new ModelAndView(LANDING_VXML).addObject("rendering_Page", vxml);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/register/")
    public ModelAndView getRegisterPage() {
        return new ModelAndView(REGISTRATION_VXML);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/menu/")
    public ModelAndView getMenuPage() {
        return new ModelAndView(MENU_VXML);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        List items = upload.parseRequest(request);

        String msisdn = getMsisdn(items);
        frontLineWorkerService.createNew(msisdn);
        String path = request.getSession().getServletContext().getRealPath("/recordings/");
        allRecordings.store(msisdn, items, path);

        return new ModelAndView(REGISTRATION_DONE_VXML);
    }

    private String getCallerId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            Object msisdn = session.getAttribute(SESSION_CALLERID_PARAM);
            if (msisdn != null) return (String) msisdn;
        }
        return request.getParameter(CALLERID_PARAM);
    }

    private String getMsisdn(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(MSISDN_PARAM))
                return item.getString();
        return null;
    }

}
