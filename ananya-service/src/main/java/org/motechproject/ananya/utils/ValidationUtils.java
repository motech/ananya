package org.motechproject.ananya.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidUUID(String uuid){
        return Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", uuid);
    }
}
