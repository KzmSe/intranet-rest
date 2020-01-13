package az.gov.adra.exception;

public class DocumentCredentialsException extends Exception {

    public DocumentCredentialsException(String message) {
        super(message);
    }

    public DocumentCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
