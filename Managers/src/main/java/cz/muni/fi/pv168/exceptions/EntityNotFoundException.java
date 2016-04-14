package cz.muni.fi.pv168.exceptions;

/**
 * Created by cechy on 21.03.2016.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs an instance of <code>EntityNotFoundException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}