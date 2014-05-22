package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum Channel {
    CONTACT_CENTER;

    public static Channel from(String chanel) {
        return Channel.valueOf(StringUtils.trimToEmpty(chanel).toUpperCase());
    }

    public static boolean isValid(String channel) {
        try {
            from(channel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
