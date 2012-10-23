
package org.motechproject.ananya.exceptions;


public class FLWDoesNotExistException extends RuntimeException {

    private FLWDoesNotExistException(String message) {
        super(message);
    }

    public static FLWDoesNotExistException withUnknownFlwGuid(String flwGuid) {
        return new FLWDoesNotExistException("Unknown flw id: " + flwGuid);
    }
}
