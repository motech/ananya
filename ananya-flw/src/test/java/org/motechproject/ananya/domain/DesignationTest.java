package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class DesignationTest {
    @Test
    public void validationMethodShouldBeCaseInsensitive() {
        boolean isInvalid = Designation.isInValid("anM");

        assertFalse(isInvalid);
    }

    @Test
    public void validationMethodShouldBeInsensitiveOfTrailingAndBeginningWhitespaces() {
        boolean isInvalid = Designation.isInValid("     anM ");

        assertFalse(isInvalid);
    }

    @Test
    public void getForShouldBeCaseInsensitive() {
        Designation designation = Designation.getFor("anM");

        assertEquals(Designation.ANM, designation);
    }

    @Test
    public void getForShouldBeInsensitiveOfTrailingAndBeginningWhitespaces() {
        Designation designation = Designation.getFor(" anM   ");

        assertEquals(Designation.ANM, designation);
    }
}
