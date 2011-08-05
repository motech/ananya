package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.ReportCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportCardsRepository extends AbstractCouchRepository<ReportCard> {
    private HealthWorkersRepository healthWorkersRepository;

    @Autowired
    public ReportCardsRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db, HealthWorkersRepository healthWorkersRepository) {
        super(ReportCard.class, db);
        this. healthWorkersRepository = healthWorkersRepository;
    }

    public ReportCard findByCallerId(String healthWorkerCallerId) {
        HealthWorker healthWorker = healthWorkersRepository.findByCallerId(healthWorkerCallerId);

        if(healthWorker == null) {
            return null;
        }

        ReportCard reportCard = this.findByHealthWorker(healthWorker);
        if(reportCard==null) {
            reportCard = new ReportCard();
            reportCard.setHealthWorkerId(healthWorker.getId());
        }
        return reportCard;
    }

    public ReportCard findByHealthWorker(HealthWorker healthWorker) {
        return this.findByHealthWorkerId(healthWorker.getId());
    }

    @Override
    public void add(ReportCard reportCard) {
        if(this.findByHealthWorkerId(reportCard.getHealthWorkerId()) == null ){
            super.add(reportCard);
        }
        else {
            this.update(reportCard);
        }
    }

    @GenerateView
    public ReportCard findByHealthWorkerId(String healthWorkerId) {
        List<ReportCard> reportCards = queryView("by_healthWorkerId", healthWorkerId);
        if(reportCards!=null && !reportCards.isEmpty()) {
            return reportCards.get(0);
        }
        return null;
    }
}