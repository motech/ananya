package org.motechproject.ananya.domain;

public class MenuLink implements Comparable<MenuLink>{

    private String displayString;

    private String url;

    private int position;

    public MenuLink(String displayString, String url, int position) {
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
    public int compareTo(MenuLink linkMenu) {
        return this.position - linkMenu.position;
    }
}

