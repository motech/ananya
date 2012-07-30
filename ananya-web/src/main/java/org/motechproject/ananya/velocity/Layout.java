package org.motechproject.ananya.velocity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Layout {
    private static Map<String, String> map = new LinkedHashMap<String, String>();

    static {
        map.put("/login", "layout/empty.vm");
    }

    public static String get(String path) {
        for (String key : map.keySet())
            if (path.matches(key))
                return map.get(key);
        return null;
    }
}
