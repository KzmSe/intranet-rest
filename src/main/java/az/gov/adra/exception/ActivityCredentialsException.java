package az.gov.adra.exception;

public class ActivityCredentialsException extends Exception {

    public ActivityCredentialsException(String message) {
        super(message);
    }

    public ActivityCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
