
package org.motechproject.ananya.exceptions;


import java.util.UUID;

public class FLWDoesNotExistException extends RuntimeException {

    private FLWDoesNotExistException(String message) {
        super(message);
    }

    public static FLWDoesNotExistException withUnknownFlwGuid(UUID flwGuid) {
        return new FLWDoesNotExistException("Unknown flw id: " + flwGuid);
    }
}
