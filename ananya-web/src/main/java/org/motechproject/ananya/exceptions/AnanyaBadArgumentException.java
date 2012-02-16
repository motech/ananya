
package org.motechproject.ananya.exceptions;


public class AnanyaBadArgumentException extends AnanyaApiException {
    
    public AnanyaBadArgumentException(String fieldName, String value) {
        super("ERR_INVALID_ARGUMENT", fieldName + " had bad value " + value);
    }
}