package org.motechproject.ananya.velocity;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AnanyaView extends VelocityLayoutView {

    @Override
    protected void doRender(Context context, HttpServletResponse response) throws Exception {
        String path = ((ChainedContext) context).getRequest().getServletPath();
        if (Layout.get(path) != null)
            setLayoutUrl(Layout.get(path));
        super.doRender(context, response);
    }

    @Override
    protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map runtimeProperties = ((CachingPropertyHolder) this.getApplicationContext().getBean("config")).resolvedProperties();
        Context context = super.createVelocityContext(model, request, response);
        context.put("app", runtimeProperties.get("app"));
        return context;
    }

}
