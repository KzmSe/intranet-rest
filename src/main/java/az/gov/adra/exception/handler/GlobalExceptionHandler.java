package az.gov.adra.exception.handler;

import az.gov.adra.entity.response.Exception;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.ActivityCredentialsException;
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
        //GenericResponse
        GenericResponse response = new GenericResponse();
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0038");
        exception.setMessage("DataAccessException occured! Please control sql statements and db.");
        exception.setErrorStack(e.getMessage());

        response.setException(exception);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setDescription("DataAccessException");
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);

        return response;
    }

    @ExceptionHandler(ActivityCredentialsException.class)
    public GenericResponse handleActivityCredentialsException(ActivityCredentialsException e) {
        //GenericResponse
        GenericResponse response = new GenericResponse();
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0039");
        exception.setMessage("ActivityCredentialsException occured! Please control id.");
        exception.setErrorStack(e.getMessage());

        response.setException(exception);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setDescription("DataAccessException");
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);

        return response;
    }

}
