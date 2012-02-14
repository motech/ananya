package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
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
    private AllLocations allLocations;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, AllLocations allLocations) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allLocations = allLocations;
    }

    public RegistrationStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker != null ? frontLineWorker.status() : RegistrationStatus.UNREGISTERED;
    }

    public boolean isCallerRegistered(String msisdn) {
        return getStatus(msisdn).isRegistered();
    }

    public String createNew(String msisdn, Designation designation, String panchayatCode) {
        Location location = allLocations.findByExternalId(panchayatCode);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, designation, location.getId()).status(RegistrationStatus.PENDING_REGISTRATION);
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

    public void resetScoresForChapterIndex(String callerId, Integer chapterIndex) {
        final FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
        save(frontLineWorker);
    }

    public void saveName(String msisdn, String name) throws Exception {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

        if(frontLineWorker == null) throw new Exception();
        name = name.trim();
        if(name.isEmpty()) throw new Exception();

        frontLineWorker.setName(name);
        frontLineWorker.setStatus(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.update(frontLineWorker);
    }
}
