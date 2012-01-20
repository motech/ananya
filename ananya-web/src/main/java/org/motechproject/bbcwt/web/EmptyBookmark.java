package org.motechproject.bbcwt.web;

import org.motechproject.ananya.domain.BookMark;

public class EmptyBookmark extends BookMark {
    @Override
    public String asJson() {
        return "{}";
    }
}
