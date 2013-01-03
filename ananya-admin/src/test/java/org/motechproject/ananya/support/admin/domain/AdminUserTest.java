package org.motechproject.ananya.support.admin.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AdminUserTest {

    @Test
    public void shouldReturnTrueIfPasswordMatches(){
        String user = "stewie";
        String password = "killLois";
        AdminUser adminUser = new AdminUser(user, password);
        assertTrue(adminUser.passwordIs(password));
        assertFalse(adminUser.passwordIs("griffin"));
        assertFalse(adminUser.passwordIs(null));
    }
}
