package org.motechproject.ananya.views;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ExceptionView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> stringObjectMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.getOutputStream().print("var ananyaResponse = \"ANANYA_ERROR\";");
    }
}
