package az.gov.adra.exception;

public class AnnouncementCredentialsException extends Exception {

    public AnnouncementCredentialsException(String message) {
        super(message);
    }

    public AnnouncementCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
