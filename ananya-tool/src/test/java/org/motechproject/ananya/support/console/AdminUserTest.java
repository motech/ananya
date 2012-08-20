package org.motechproject.ananya.support.console;

import org.junit.Test;
import org.motechproject.ananya.support.admin.domain.AdminUser;

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
