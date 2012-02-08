package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private FrontLineWorkerService flwService;
    private AllRecordings allRecordings;

    @Autowired
    public RegistrationController(FrontLineWorkerService flwService, AllRecordings allRecordings) {
        this.flwService = flwService;
        this.allRecordings = allRecordings;
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/register/")
    @ResponseBody
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        String msisdn = request.getParameter("session.connection.remote.uri");
        String designation = request.getParameter("designation");
        String panchayat = request.getParameter("panchayat");

        flwService.createNew(msisdn, Designation.valueOf(designation), panchayat);

        log.info("Registered new FLW:" + msisdn);
        return new ModelAndView("register-done");
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/record/name/")
    @ResponseBody
    public ModelAndView recordName(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = getUploader();
        List items = upload.parseRequest(request);

        String msisdn = getField(items, "session.connection.remote.uri");
        String realPath = request.getSession().getServletContext().getRealPath("/recordings/");

        allRecordings.store(msisdn, items, realPath);

        log.info("Recorded new FLW name:" + msisdn);
        return new ModelAndView("register-done");
    }

    protected ServletFileUpload getUploader() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    private String getField(List<FileItem> items, String key) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(key))
                return item.getString();
        return null;
    }
}
