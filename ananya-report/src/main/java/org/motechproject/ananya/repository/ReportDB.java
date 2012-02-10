package org.motechproject.ananya.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDB {

    @Autowired
    private DataAccessTemplate template;

    public <T> void add(T dataBean) {
        template.save(dataBean);
    }

}
