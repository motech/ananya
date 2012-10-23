package org.motechproject.ananya.domain;

public enum Channel {
    CONTACT_CENTER;

    public static boolean isInvalid(String channel) {
        for(Channel correctChannel : Channel.values()){
            if(correctChannel.name().equalsIgnoreCase(channel.trim()))
                return false;
        }

        return true;
    }
}
