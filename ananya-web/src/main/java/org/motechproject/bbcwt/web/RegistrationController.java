package org.motechproject.bbcwt.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Controller
@Scope(value = "prototype")
public class RegistrationController {
    private static final String REGISTRATION_VXML = "register-flw";
    public static final String XML = "text/xml";
    public static final String MSISDN_PARAM = "msisdn";

    @Value("#{ananyaProperties['recorded.wav.files.path']}")
    private String wavFilesLocation;

    @RequestMapping(method = {RequestMethod.GET}, value = "/flw/register/vxml/")
    public ModelAndView forNameAndLocation(HttpServletResponse response) {
        response.setContentType(XML);
        return new ModelAndView(REGISTRATION_VXML);
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.POST}, value = "/flw/register/")
    public String createNew(HttpServletRequest request) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        String msisdn = getCallerId(items);

        for (FileItem item : items) {
            if (item.isFormField()) continue;
            File savedFile = new File(msisdn + "_" + item.getFieldName() + ".wav");
            savedFile.createNewFile();
            item.write(savedFile);
        }
        return msisdn;
    }

    private String getCallerId(List<FileItem> items) {
        for (FileItem item : items)
            if (item.isFormField() && item.getFieldName().equals(MSISDN_PARAM))
                return item.getString();
        return null;
    }


}
