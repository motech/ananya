package org.motechproject.ananya.views;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class JsonView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {

        Object serializableObject = model.get("root");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsonObject = gsonBuilder.create();

        response.setContentType("application/json");
        response.getOutputStream().print(gsonObject.toJson(serializableObject));
    }
}
