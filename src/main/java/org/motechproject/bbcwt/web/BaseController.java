package org.motechproject.bbcwt.web;

import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public abstract class BaseController {

    protected String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
        return pathSegment;
    }
}
