package org.motechproject.ananya.support.admin.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.support.admin.domain.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-admin.xml")
public class AllAdminUsersTest {

    @Autowired
    private AllAdminUsers allAdminUsers;

    @Test
    public void shouldFindByName() {
        allAdminUsers.add(new AdminUser("cartman", "killHippie"));

        AdminUser adminUser = allAdminUsers.findByName("cartman");
        assertEquals("cartman",adminUser.getName());
        assertTrue(adminUser.passwordIs("killHippie"));
    }

    @Test
    public void shouldReturnNullIfUserNameIsEmpty(){
        assertNull(allAdminUsers.findByName(""));
        assertNull(allAdminUsers.findByName(null));
    }

    @After
    public void tearDown() {
        allAdminUsers.removeAll();
    }

}
