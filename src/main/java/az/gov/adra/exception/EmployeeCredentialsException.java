package az.gov.adra.exception;

public class EmployeeCredentialsException extends Exception {

    public EmployeeCredentialsException(String message) {
        super(message);
    }

    public EmployeeCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
