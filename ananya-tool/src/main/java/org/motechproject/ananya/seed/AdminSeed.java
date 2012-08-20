package org.motechproject.ananya.seed;

import org.motechproject.ananya.support.admin.domain.AdminUser;
import org.motechproject.ananya.support.admin.repository.AllAdminUsers;
import org.motechproject.deliverytools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminSeed {

    private static final Logger log = LoggerFactory.getLogger(AdminSeed.class);

    @Autowired
    private AllAdminUsers allAdminUsers;

    @Seed(priority = 0, version = "1.7", comment = "Create and persist admin user for console in couchdb")
    public void createAdminConsoleUser() {
        if(allAdminUsers.findByName("admin") != null){
            log.info("Admin user already exists.");
            return;
        }
        allAdminUsers.add(new AdminUser("admin", "p@ssw0rd"));
        log.info("created admin user for prod console");

    }
}
