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
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private FrontLineWorkerService frontLineWorkerService;
    private AllRecordings allRecordings;

    @Autowired
    public RegistrationController(FrontLineWorkerService frontLineWorkerService, AllRecordings allRecordings) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRecordings = allRecordings;
    }

    @RequestMapping(value = "/vxml/register")
    public ModelAndView getCallFlow(){
        return new ModelAndView("register-flw");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = getUploader();
        List items = upload.parseRequest(request);

        String msisdn = getMsisdn(items);
        frontLineWorkerService.createNew(msisdn);
        String path = request.getSession().getServletContext().getRealPath("/recordings/");
        allRecordings.store(msisdn, items, path);

        log.info("Registered new FLW:"+msisdn);
        return new ModelAndView("register-done-flw");
    }


    public boolean isCallerRegistered(String msisdn) {
        return frontLineWorkerService.getStatus(msisdn).isRegistered();
    }

    protected ServletFileUpload getUploader() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    private String getMsisdn(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals("msisdn"))
                return item.getString();
        return null;
    }
}
