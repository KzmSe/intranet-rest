package az.gov.adra.exception.handler;

import az.gov.adra.entity.response.Exception;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.exception.EmployeeCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.app-name}")
    private String appName;

    @ExceptionHandler(DataAccessException.class)
    public GenericResponse handleDataAccessException(DataAccessException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0038");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DataAccessException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "DataAccessException", exception);
    }

    @ExceptionHandler(ActivityCredentialsException.class)
    public GenericResponse handleActivityCredentialsException(ActivityCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0039");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("ActivityCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "ActivityCredentialsException", exception);
    }

    @ExceptionHandler(EmployeeCredentialsException.class)
    public GenericResponse handleEmployeeCredentialsException(EmployeeCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0040");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("EmployeeCredentialsException.");

        return GenericResponse.withException(HttpStatus.BAD_REQUEST, "EmployeeCredentialsException", exception);
    }

}
