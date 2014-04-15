package org.motechproject.ananya.velocity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Layout {
    private static Map<String, String> map = new LinkedHashMap<String, String>();

    static {
        map.put("/admin/login", "layout/admin-login.vm");
        map.put("/admin/logs", "layout/admin-default.vm");
        map.put("/admin/monitor", "layout/admin-default.vm");
        map.put("/admin/inquiry", "layout/admin-default.vm");
        map.put("/internal/admin/monitor", "layout/admin-default.vm");
        map.put("/internal/admin/logs", "layout/admin-default.vm");
    }

    public static String get(String path) {
        for (String key : map.keySet())
            if (path.matches(key))
                return map.get(key);
        return null;
    }
}
