package org.motechproject.ananya.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CallerIdParamTest {

    @Test
    public void shouldReturnCorrectedCallerId(){
        CallerIdParam callerIdParam1 = new CallerIdParam("9986574420");
        assertThat(callerIdParam1.getValue(), is("919986574420"));

        CallerIdParam callerIdParam2 = new CallerIdParam("919986574420");
        assertThat(callerIdParam2.getValue(), is("919986574420"));

    }
}
