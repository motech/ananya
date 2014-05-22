package org.motechproject.ananya.domain.page;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static junit.framework.Assert.assertEquals;

public class LoginPageTest {

    @Test
    public void shouldReturnViewWithError() {
        LoginPage loginPage = new LoginPage();
        ModelAndView modelAndView = loginPage.display("error");

        assertEquals("admin/login", modelAndView.getViewName());
        assertEquals("error", modelAndView.getModel().get("error"));
    }

    @Test
    public void shouldPrintIP() throws Exception {



    }


}
