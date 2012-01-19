package org.motechproject.bbcwt.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtil {


    private static final String CALLERID_PARAM = "session.callerid";
    private static final String SESSION_CALLERID_PARAM = "session.connection.remote.uri";

    public static String getCallerId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            Object msisdn = session.getAttribute(SESSION_CALLERID_PARAM);
            if (msisdn != null) return (String) msisdn;
        }
        return request.getParameter(CALLERID_PARAM);
    }
}
