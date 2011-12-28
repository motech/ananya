package org.motechproject.bbcwt.tools.maintenance;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class DeleteMilestonesHistory {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        String number = System.getProperty("dialed.in.number");
        CouchDbConnector db = context.getBean("ananyaDbConnector", CouchDbConnector.class);

        HealthWorkersRepository healthWorkers = context.getBean(HealthWorkersRepository.class);
        MilestonesRepository milestones = context.getBean(MilestonesRepository.class);

        if(number == null) {
            List<BulkDeleteDocument> docsToDelete = new ArrayList<BulkDeleteDocument>();
            for(Milestone eachMilestone : milestones.getAll()) {
                HealthWorker healthWorker = healthWorkers.get(eachMilestone.getHealthWorkerId());
                docsToDelete.add(BulkDeleteDocument.of(healthWorker));
                docsToDelete.add(BulkDeleteDocument.of(eachMilestone));
            }
            db.executeBulk(docsToDelete);
        }
        else {
            HealthWorker healthWorker = healthWorkers.findByCallerId(number);
            if(healthWorker != null) {
                Milestone milestone = milestones.findByHealthWorker(healthWorker);
                milestones.remove(milestone);
            }
            healthWorkers.remove(healthWorker);
        }
    }
}