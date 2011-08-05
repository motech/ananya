package org.motechproject.bbcwt.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.motechproject.bbcwt.matcher.ReportCardMatcher.hasResponse;

public class ReportCardsRepositoryTest extends SpringIntegrationTest{
    @Autowired
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    private HealthWorkersRepository healthWorkersRepository;

    private Chapter chapter;
    private Question question;
    private HealthWorker healthWorker;
    private int response;
    private ReportCard reportCard;
    private String callerId;
    private ReportCard.HealthWorkerResponseToQuestion response1;

    @Before
    public void setUp(){
        callerId = "9876543210";
        chapter = new Chapter(1);
        chapter.setId(UUIDUtil.newUUID());
        question = new Question(1, null, null, -1, null, null);
        healthWorker = new HealthWorker();
        healthWorker.setCallerId(callerId);
        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);
        response = 1;
        reportCard = new ReportCard(healthWorker.getId());
        response1 = reportCard.recordResponse(chapter, question, response);
    }

    @Test
    public void shouldPersistAHealthWorkerReportCard(){
        reportCardsRepository.add(reportCard);
        markForDeletion(reportCard);

        ReportCard reportCardFromDB = reportCardsRepository.get(reportCard.getId());
        ReportCard.HealthWorkerResponseToQuestion expectedResponse =  new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question.getId(), response);

        assertNotNull(reportCard.getId());
        assertEquals(reportCard.getId(), reportCardFromDB.getId());
        assertEquals(reportCardFromDB.getHealthWorkerId(), healthWorker.getId());
        assertThat(reportCardFromDB, hasResponse(expectedResponse));
    }

    @Test
    public void shouldReturnAHealthWorkerGivenAHealthWorkerId(){
        reportCardsRepository.add(reportCard);
        markForDeletion(reportCard);

        ReportCard reportCardFromDB = reportCardsRepository.findByHealthWorkerId(healthWorker.getId());

        assertEquals(reportCardFromDB.getHealthWorkerId(), healthWorker.getId());
    }

    @Test
    public void shouldReturnAHealthWorkerGivenACallerId(){
        reportCardsRepository.add(reportCard);
        markForDeletion(reportCard);

        ReportCard reportCardFromDB = reportCardsRepository.findByCallerId(callerId);

        assertEquals(reportCardFromDB.getHealthWorkerId(), healthWorker.getId());
    }

    @Test
    public void addShouldUpdateExistingReportCardIfTheHealthWorkerAlreadyHasOne() {
        reportCardsRepository.add(reportCard);
        markForDeletion(reportCard);

        Question question2 = new Question(2, null, null, -1, null, null);

        ReportCard.HealthWorkerResponseToQuestion response2 = reportCard.recordResponse(chapter, question2, 1);

        reportCardsRepository.add(reportCard);

        ReportCard updatedReportCardFromDB = reportCardsRepository.get(reportCard.getId());
        markForDeletion(updatedReportCardFromDB);

        assertThat(updatedReportCardFromDB, hasResponse(response1));
        assertThat(updatedReportCardFromDB, hasResponse(response2));
    }
}