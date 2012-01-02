package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.motechproject.bbcwt.repository.AllRecordings;
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
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static final String registration_vxml = "register-flw";
    private static final String menu_vxml = "top-menu";
    private static final String xml = "text/xml";
    private static final String MSISDN_PARAM = "msisdn";

    private FrontLineWorkerService frontLineWorkerService;
    private AllRecordings allRecordings;

    @Autowired
    public RegistrationController(FrontLineWorkerService frontLineWorkerService, AllRecordings allRecordings) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRecordings = allRecordings;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/flw/vxml/")
    public ModelAndView callFlow(HttpServletRequest request, HttpServletResponse response) {
        String msisdn = request.getParameter("msisdn");
        response.setContentType(xml);
        String vxml = frontLineWorkerService.getStatus(msisdn).isRegistered() ? menu_vxml : registration_vxml;
        return new ModelAndView(vxml);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public String registerNew(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        List items = upload.parseRequest(request);

        String msisdn = getMsisdn(items);
        frontLineWorkerService.createNew(msisdn);

        String path = request.getSession().getServletContext().getRealPath("/recordings/");
        allRecordings.store(msisdn, items, path);

        return msisdn;
    }

    private String getMsisdn(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(MSISDN_PARAM))
                return item.getString();
        return null;
    }

}
