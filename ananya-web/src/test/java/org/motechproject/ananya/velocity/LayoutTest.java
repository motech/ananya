package org.motechproject.ananya.velocity;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LayoutTest {

    @Test
    public void shouldReturnLayoutTemplateForAdminUrls(){
        assertThat(Layout.get("/admin/monitor"), is("layout/admin-default.vm"));
        assertThat(Layout.get("/admin/inquiry"), is("layout/admin-default.vm"));
        assertThat(Layout.get("/admin/login"), is("layout/admin-login.vm"));
        assertNull(Layout.get("/admin23/inquiry"));
        assertNull(Layout.get("/generated/js/callerdata.js"));
    }
}
