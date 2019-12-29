package az.gov.adra.exception;

public class PostCredentialsException extends Exception {

    public PostCredentialsException(String message) {
        super(message);
    }

    public PostCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
