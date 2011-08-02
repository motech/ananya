package org.motechproject.bbcwt.util;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateUtil {
    public Date getDate() {
        return new Date(System.currentTimeMillis());
    }
}