
package org.motechproject.ananya.views;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.InternalResourceView;

public class JsonView extends AbstractView{

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, 
        HttpServletRequest request, HttpServletResponse response) throws Exception {
    
        System.out.println("inside json view");
        
        Object serializableObject = model.get("root");
        
        System.out.println("object root fetched is : " + serializableObject);
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsonObject = gsonBuilder.create();
        
        String serializedString = gsonObject.toJson(serializableObject);
        
        System.out.println("Serialized string is " + serializedString);
        
        response.setContentType("application/json");
        response.getOutputStream().print(serializedString);
    }
}
