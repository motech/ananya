package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.ananya.response.ValidationResponse;
import org.motechproject.ananya.utils.DateUtils;

import java.util.regex.Pattern;

public class WebRequestValidator {

    public void validateChannel(String channel, ValidationResponse validationResponse) {
        if (StringUtils.isEmpty(channel)) {
            validationResponse.addError("missing channel");
            return;
        }
        if (!Channel.isValid(channel)) {
            validationResponse.addError(String.format("invalid channel: %s", channel));
        }
    }

    public void validateMsisdn(String msisdn, ValidationResponse validationResponse) {
        if (!new PhoneNumber(msisdn).isValid())
            validationResponse.addError("invalid msisdn: " + msisdn);
    }

    public void validateDateRange(String startDate, String endDate, ValidationResponse validationResponse) {
        LocalDate startLocalDate = getLocalDate(startDate);
        LocalDate endLocalDate = getLocalDate(endDate);

        if (startLocalDate != null && endLocalDate != null) {
            if (!startLocalDate.isBefore(endLocalDate)) {
                validationResponse.addError(String.format("start date should be before end date"));
            }
            return;
        }

        if (startLocalDate == null) {
            validationResponse.addError(String.format("invalid start date: %s", startDate));
        }

        if (endLocalDate == null) {
            validationResponse.addError(String.format("invalid end date: %s", endDate));
        }
    }

    public LocalDate getLocalDate(String value) {
        if (StringUtils.isEmpty(value) || !Pattern.matches("^\\d{2}-\\d{2}-\\d{4}$", value)) {
            return null;
        }
        try {
            LocalDate date = DateUtils.parseLocalDate(value);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
}
