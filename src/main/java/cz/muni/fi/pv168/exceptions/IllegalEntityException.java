package cz.muni.fi.pv168.exceptions;

/**
 * @author Peter Hutta
 * @version 1.0  30.3.2016
 */
public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException() {
    }

    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}