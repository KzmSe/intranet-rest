package az.gov.adra.exception;

public class UserCredentialsException extends Exception {

    public UserCredentialsException(String message) {
        super(message);
    }

    public UserCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
