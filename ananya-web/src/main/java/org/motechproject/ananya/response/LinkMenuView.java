package org.motechproject.ananya.response;

public class LinkMenuView implements Comparable<LinkMenuView>{

    private String displayString;

    private String url;

    private int position;

    public LinkMenuView(String displayString, String url, int position) {
        this.displayString = displayString;
        this.url = url;
        this.position = position;
    }

    public String getDisplayString() {
        return displayString;
    }

    public int getPosition() {
        return position;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int compareTo(LinkMenuView linkMenuView) {
        return this.position - linkMenuView.position;
    }
}

