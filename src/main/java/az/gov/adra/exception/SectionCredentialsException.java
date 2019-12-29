package az.gov.adra.exception;

public class SectionCredentialsException extends Exception {

    public SectionCredentialsException(String message) {
        super(message);
    }

    public SectionCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
