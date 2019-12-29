package az.gov.adra.exception;

public class DepartmentCredentialsException extends Exception {

    public DepartmentCredentialsException(String message) {
        super(message);
    }

    public DepartmentCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
