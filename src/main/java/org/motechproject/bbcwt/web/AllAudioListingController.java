package org.motechproject.bbcwt.web;


import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AllAudioListingController extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IVRResponseBuilder ivrResponseBuilder = new IVRResponseBuilder();
        int count = 1;
        for(Object path : getServletContext().getResourcePaths("/audio/")) {
            ivrResponseBuilder.addPlayText("Playing audio no: " + count++);
            ivrResponseBuilder.addPlayAudio("http://119.82.102.200/bbcwt" + path);
        }
        ivrResponseBuilder.withHangUp();
        resp.setContentType("text/xml");
        resp.getWriter().append(ivrResponseBuilder.create().getXML());
    }
}