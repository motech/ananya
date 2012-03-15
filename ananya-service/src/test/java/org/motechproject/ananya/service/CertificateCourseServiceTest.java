package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CertificateCourseServiceTest {

    private CertificateCourseService certificateCourseService;

    @Mock
    private FrontLineWorkerService frontlineWorkerService;
    @Mock
    private CertificateCourseLogService certificateCourseLogService;


    @Before
    public void setUp() {
        initMocks(this);
        certificateCourseService = new CertificateCourseService(certificateCourseLogService, frontlineWorkerService);
    }

    @Test
    public void shouldCreateCallerDataForGivenCallerId() {
        String callerId = "123";
        String operator = "airtel";

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);

        when(frontlineWorkerService.createOrUpdate(callerId, operator)).thenReturn(frontLineWorker);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(callerId, operator);
        assertEquals(bookMark.asJson(), callerData.getBookmark());

    }
}
