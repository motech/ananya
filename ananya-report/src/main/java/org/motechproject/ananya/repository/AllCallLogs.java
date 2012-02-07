package org.motechproject.ananya.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallLogs {

    @Autowired
    private DataAccessTemplate template;

    public void add(Object dataBean) {
        template.save(dataBean);
    }
}
