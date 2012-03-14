
package org.motechproject.ananya.exceptions;

public class AnanyaException extends Exception {
    
    public AnanyaException() {
        super();
        
        // TODO: Log and email exceptions here.
    }
    
    public AnanyaException(Exception e) {
        super(e);
    }
    
    public AnanyaException(String message, Exception e) {
        super(message, e);
    }
}
