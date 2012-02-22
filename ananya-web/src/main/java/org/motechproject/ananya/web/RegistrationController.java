package org.motechproject.ananya.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllRecordings;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.ReportDataPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private FrontLineWorkerService flwService;
    private ReportDataPublisher reportPublisher;
    private AllRecordings allRecordings;
    private RegistrationLogService logService;


    @Autowired
    public RegistrationController(FrontLineWorkerService flwService, AllRecordings allRecordings,
                                  RegistrationLogService logService, ReportDataPublisher reportPublisher) {
        this.logService = logService;
        this.allRecordings = allRecordings;
        this.flwService = flwService;
        this.reportPublisher = reportPublisher;
    }

    @RequestMapping(method = RequestMethod.POST, value = "flw/register")
    public ModelAndView registerNew(HttpServletRequest request) throws Exception {
        try {
            String callerId = request.getParameter("session.connection.remote.uri");
            String calledNumber = request.getParameter("session.connection.local.uri");
            String designation = request.getParameter("designation");
            String panchayat = request.getParameter("panchayat");

            log.info("callerid=" + callerId + "|calledNo=" + calledNumber + "|designation=" + designation + "|panchayat=" + panchayat);

            flwService.createNew(callerId, Designation.valueOf(designation), panchayat);

            RegistrationLog registrationLog = new RegistrationLog(callerId, calledNumber, DateTime.now(), DateTime.now(), "");
            registrationLog.designation(designation).panchayat(panchayat);
            logService.addNew(registrationLog);

            LogData logData = new LogData(LogType.REGISTRATION, registrationLog.getId());
            reportPublisher.publishRegistration(logData);

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
    @ResponseBody
    public void saveTranscribedName(HttpServletRequest request) throws Exception {
        try {
            String msisdn = request.getParameter("msisdn");
            String name = request.getParameter("name");

            FrontLineWorker savedFrontLineWorker = flwService.saveName(msisdn, name);
            LogData logData = new LogData(LogType.REGISTRATION_SAVE_NAME, savedFrontLineWorker.getId());

            reportPublisher.publishRegistrationUpdate(logData);
            log.info("Saved Transcribed name for:" + savedFrontLineWorker);

        } catch (Exception e) {
            log.error("Exception:", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "vxml/register/block")
    public ModelAndView getBlockVxml(@RequestParam String district) {
        return new ModelAndView("block").addObject("district",district);
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
