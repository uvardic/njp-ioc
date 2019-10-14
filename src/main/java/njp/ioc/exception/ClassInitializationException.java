package njp.ioc.exception;

public class ClassInitializationException extends RuntimeException {

    public ClassInitializationException(String message) {
        super(message);
    }

    public ClassInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
