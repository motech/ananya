package org.motechproject.ananya.views;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class DataAPIExceptionView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> stringObjectMap,
            HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        httpServletResponse.getOutputStream().print(stringObjectMap.get("exception").toString());
    }
}
