
package org.motechproject.ananya.exceptions;


import java.util.UUID;

public class FLWDoesNotExistException extends RuntimeException {

    private FLWDoesNotExistException(String message) {
        super(message);
    }

    public static FLWDoesNotExistException withUnknownFlwId(UUID flwId) {
        return new FLWDoesNotExistException("Unknown flw id: " + flwId);
    }
}
