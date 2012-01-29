package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.bbcwt.repository.AllRecordings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/vxml/{entry}/register")
    public ModelAndView getCallFlow(HttpServletRequest request, @PathVariable String entry) {
        String contextPath = request.getContextPath();
        String nextFlow = entry.equals("jobaid") ? contextPath + "/vxml/jobaid.vxml" : contextPath + "/vxml/certificatecourse.vxml";

        Map<Integer, String> designations = new HashMap<Integer, String>();
        designations.put(1, Designation.ANM.name());
        designations.put(2, Designation.ASHA.name());
        designations.put(3, Designation.ANGANWADI.name());

        return new ModelAndView("register").addObject("nextFlow", nextFlow).addObject("designations", designations);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/flw/register/")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        ServletFileUpload upload = getUploader();
        List items = upload.parseRequest(request);

        String msisdn = getField(items, "session.connection.remote.uri");
        String designation = getField(items, "designation");

        flwService.createNew(msisdn, Designation.valueOf(designation));
        allRecordings.store(msisdn, items, request.getSession().getServletContext().getRealPath("/recordings/"));

        log.info("Registered new FLW:" + msisdn);
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
