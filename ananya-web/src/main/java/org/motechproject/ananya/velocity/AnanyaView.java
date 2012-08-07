package org.motechproject.ananya.velocity;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import javax.servlet.http.HttpServletResponse;

public class AnanyaView extends VelocityLayoutView {

    @Override
    protected void doRender(Context context, HttpServletResponse response) throws Exception {
        String path = ((ChainedContext) context).getRequest().getServletPath();
        if (Layout.get(path) != null)
            setLayoutUrl(Layout.get(path));
        super.doRender(context, response);
    }

}
