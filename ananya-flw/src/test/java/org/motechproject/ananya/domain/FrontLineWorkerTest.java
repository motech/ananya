package org.motechproject.ananya.domain;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FrontLineWorkerTest {
    @Test
    public void shouldReturnEmptyBookmarkIfThereIsNoBookmark() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", Designation.ANGANWADI);
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }
}
