package org.motechproject.ananya.domain;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FrontLineWorkerTest {
    @Test
    public void shouldReturnEmptyBookmarkIfThereIsNoBookmark() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", Designation.ANGANWADI, "locationId","");
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }

    @Test
    public void shouldHaveStartedCertificationCourse() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", Designation.ANGANWADI, "locationId","");
        flw.status(RegistrationStatus.REGISTERED);

        assertThat(flw.bookMark(), is(EmptyBookmark.class));
        assertFalse(flw.hasStartedCertificationCourse());

        flw.addBookMark(new BookMark("lesson",0,1));
        assertTrue(flw.hasStartedCertificationCourse());
    }
}
