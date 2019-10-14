package njp.ioc.exception;

public class ClassMappingException extends RuntimeException {

    public ClassMappingException() {
        super();
    }

    public ClassMappingException(String message) {
        super(message);
    }

    public ClassMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
