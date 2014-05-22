package org.motechproject.ananya.domain.page;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class LoginPage {

    private String viewName = "admin/login";

    public ModelAndView display(String error) {
        return new ModelAndView(viewName).addObject("error", error);
    }
}
