package org.motechproject.bbcwt.web;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;

@Controller
public class AdminController {

    public static final String admin = "admin";

    @RequestMapping(method = RequestMethod.GET, value = "/recordings")
    public ModelAndView showPage() {
        return new ModelAndView(admin);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/recordings")
    public ModelAndView showWaves(@RequestParam String msisdn, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/recordings/");
        FileFilter fileFilter = new WildcardFileFilter(msisdn + "*");
        File dir = new File(path);
        return new ModelAndView(admin).addObject("waves", dir.listFiles(fileFilter));
    }
}
