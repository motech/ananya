package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IVRAction {
    String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response);
}
