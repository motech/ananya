package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllOperators extends MotechBaseRepository<Operator> {
    @Autowired
    protected AllOperators(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(Operator.class, db);
        initStandardDesignDocument();
    }

    public void add(Operator operator) {
        super.add(operator);
    }

    @GenerateView
    public Operator findByName(String operatorName) {
        ViewQuery viewQuery = createQuery("by_name").key(operatorName).includeDocs(true);
        List<Operator> operators = db.queryView(viewQuery, Operator.class);
        if (operators == null || operators.isEmpty()) return null;
        return operators.get(0);
    }
}
