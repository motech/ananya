package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FrontLineWorkerService {
    private static Logger log = LoggerFactory.getLogger(FrontLineWorkerService.class);

    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public RegistrationStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker != null ? frontLineWorker.status() : RegistrationStatus.UNREGISTERED;
    }

    public boolean isCallerRegistered(String msisdn) {
        return getStatus(msisdn).isRegistered();
    }

    public String createNew(String msisdn, Designation designation) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, designation).status(RegistrationStatus.PENDING_REGISTRATION);
        allFrontLineWorkers.add(frontLineWorker);
        return msisdn;
    }

    public FrontLineWorker getFrontLineWorker(String msisdn) {
        return allFrontLineWorkers.findByMsisdn(msisdn);
    }

    public void addBookMark(String callerId, BookMark bookMark){
        FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.addBookMark(bookMark);
        save(frontLineWorker);
    }

    public void save(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
    }

    public void addScore(String callerId, ReportCard.Score score) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.reportCard().addScore(score);
        save(frontLineWorker);
    }

    public BookMark getBookmark(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker == null ? new EmptyBookmark() : frontLineWorker.bookMark();
    }

    public Map<String, Integer> scoresByChapter(String msisdn){
        final FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker == null? new HashMap() : frontLineWorker.reportCard().scoresByChapterIndex();
    }
}
