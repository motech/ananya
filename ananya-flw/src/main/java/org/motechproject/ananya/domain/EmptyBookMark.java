package org.motechproject.ananya.domain;

public class EmptyBookmark extends BookMark {
    @Override
    public String asJson() {
        return "{}";
    }
}