package az.gov.adra.exception;

public class CommandCredentialsException extends Exception {

    public CommandCredentialsException(String message) {
        super(message);
    }

    public CommandCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
