package org.motechproject.bbcwt.listeners;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SendSMSHandlerTest {
    @Test
    public void shouldKeepTheNumberAsItIsIfTheNumberOfDigitsIs10() {
        SendSMSHandler smsHandler = new SendSMSHandler();
        String number = "1234567890";
        assertThat(smsHandler.last10DigitsOfNumber(number), is(number));
    }

    @Test
    public void shouldGiveLast10DigitsIfTheNumberOfDigitsExceeds10() {
        SendSMSHandler smsHandler = new SendSMSHandler();
        String number = "abcd1234567890";
        assertThat(smsHandler.last10DigitsOfNumber(number), is("1234567890"));
    }

    @Test
    public void shouldKeepTheNumberAsItIsIfTheNumberOfDigitsIsLessThan10() {
        SendSMSHandler smsHandler = new SendSMSHandler();
        String number = "123456";
        assertThat(smsHandler.last10DigitsOfNumber(number), is(number));
    }
}