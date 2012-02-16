
package org.motechproject.ananya.exceptions;


public class AnanyaArgumentMissingException extends AnanyaApiException {
    
    public AnanyaArgumentMissingException(String fieldName) {
        super("ERR_MISSING_ARGUMENT", fieldName + " is missing");
    }
}