package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.dao.MotechBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllOperators extends MotechBaseRepository<Operator> {
	
	  private static Logger log = LoggerFactory.getLogger(AllOperators.class);

    @Autowired
    protected AllOperators(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(Operator.class, db);
        initStandardDesignDocument();
    }

    public void add(Operator operator) {
        super.add(operator);
    }

   /* @GenerateView
    public Operator findByName(String operatorName) {
        ViewQuery viewQuery = createQuery("by_name").key(operatorName).includeDocs(true);
        List<Operator> operators = db.queryView(viewQuery, Operator.class);
        if (operators == null || operators.isEmpty()) return null;
        return operators.get(0);
    }*/
    
    @GenerateView
    public Operator findByName(String operatorName, String circle) {
    	log.info("queryview");
        ViewQuery viewQuery = createQuery("by_name").key(operatorName).includeDocs(true);
        List<Operator> operators = db.queryView(viewQuery, Operator.class);
        log.info("fetched operators list");
        if (operators == null || operators.isEmpty()){
        	log.info("returning null");
        }
        if (operators == null || operators.isEmpty()) return null;
        Operator operatorToReturn = operators.get(0);
        log.info("operator not null");
        for(Operator operator:operators){
        	if(operator.getCircle()!=null && operator.getCircle().equalsIgnoreCase(circle))
        		return operator;
        	if(operator.getCircle()==null)
        		operatorToReturn = operator;
        }
        return operatorToReturn;
    }
    
}
