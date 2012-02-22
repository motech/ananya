package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.RegistrationRequest;
import org.motechproject.ananya.domain.ResponseStatus;
import org.motechproject.ananya.exceptions.AnanyaApiException;
import org.motechproject.ananya.exceptions.AnanyaArgumentMissingException;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private AllRecordings allRecordings;

    private RegistrationService registrationService;


    @Autowired
    public RegistrationController(AllRecordings allRecordings, RegistrationService registrationService) {
        this.allRecordings = allRecordings;
        this.registrationService = registrationService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/register")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        try {
            String callerId = request.getParameter("session.connection.remote.uri");
            String calledNumber = request.getParameter("session.connection.local.uri");
            String designation = request.getParameter("designation");
            String panchayat = request.getParameter("panchayat");

            RegistrationRequest registrationRequest = new RegistrationRequest(callerId, calledNumber, designation, panchayat, "");
            registrationService.register(registrationRequest);

            log.info("callerid=" + callerId + "|calledNo=" + calledNumber + "|designation=" + designation + "|panchayat=" + panchayat);
            log.info("Registered new FLW:" + callerId);

        } catch (Exception e) {
            log.error("Exception:", e);
            throw e;
        }
        return new ModelAndView("register-done");
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/record/name")
    public ModelAndView recordName(HttpServletRequest request) throws Exception {
        try {
            ServletFileUpload upload = getUploader();
            List items = upload.parseRequest(request);
            String callerId = getField(items, "session.connection.remote.uri");
            String realPath = request.getSession().getServletContext().getRealPath("/recordings/");

            allRecordings.store(callerId, items, realPath);

            log.info("Recorded new FLW name:" + callerId);
            return new ModelAndView("register-done");

        } catch (Exception e) {
            log.error("Exception:", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/save/name")
    public ModelAndView saveTranscribedName(HttpServletRequest request) throws AnanyaApiException {
        String msisdn = request.getParameter("msisdn");
        String name = request.getParameter("name");

        if (StringUtils.isBlank(msisdn))
            throw new AnanyaArgumentMissingException("msisdn");
        if (StringUtils.isBlank(name))
            throw new AnanyaArgumentMissingException(("name"));
        try {
            registrationService.saveTranscribedName(msisdn,name);
        } catch (WorkerDoesNotExistException e) {
            throw new AnanyaApiException("ERR_WORKER_DOES_NOT_EXIST","Worker does not exist for mentioned mobile number.");
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("root", new ResponseStatus("SUCCESS", "Successful"));

        return new ModelAndView("jsonView", model);
    }

    @ExceptionHandler(AnanyaApiException.class)
    public ModelAndView handleException(AnanyaApiException ex) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("root", new ResponseStatus(ex.getErrorCode(), ex.getErrorMessage()));
        return new ModelAndView("jsonView", model);
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
