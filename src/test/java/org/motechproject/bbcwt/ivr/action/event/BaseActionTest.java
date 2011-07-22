package org.motechproject.bbcwt.ivr.action.event;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.motechproject.bbcwt.ivr.IVRMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class BaseActionTest {
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;

}
