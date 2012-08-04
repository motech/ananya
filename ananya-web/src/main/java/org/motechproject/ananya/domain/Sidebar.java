package org.motechproject.ananya.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sidebar {

    public Map<String, List<MenuLink>> getMenu() {
        Map<String, List<MenuLink>> menu = new HashMap<String, List<MenuLink>>();

        List<MenuLink> links = new ArrayList<MenuLink>();
        links.add(new MenuLink("Monitor", "admin/monitor", 0));
        links.add(new MenuLink("Inquiry", "admin/inquiry", 0));

        menu.put("Production", links);
        return menu;
    }
}
